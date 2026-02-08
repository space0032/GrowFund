package com.growfund.service;

import com.growfund.dto.CropDTO;
import com.growfund.model.Crop;
import com.growfund.model.Farm;
import com.growfund.repository.CropRepository;
import com.growfund.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CropService {

    private final CropRepository cropRepository;
    private final FarmRepository farmRepository;
    private final WeatherService weatherService;
    private final AchievementService achievementService;
    private final RandomEventService randomEventService;
    private final EquipmentService equipmentService;
    private final Random random = new Random();

    private static final Long MIN_INVESTMENT_PER_ACRE = 5000L;
    private static final Long STANDARD_INVESTMENT_PER_ACRE = 10000L;

    @Transactional
    public CropDTO plantCrop(Long farmId, String cropType, Double areaPlanted, Long investmentAmount, String season) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new com.growfund.exception.ResourceNotFoundException("Farm not found"));

        if (areaPlanted <= 0) {
            throw new IllegalArgumentException("Area planted must be greater than 0");
        }

        // 1. Strict Acreage Validation
        // Calculate used land from active crops
        List<Crop> activeCrops = cropRepository.findByFarmId(farmId).stream()
                .filter(c -> "PLANTED".equals(c.getStatus()) || "GROWING".equals(c.getStatus())
                        || "READY".equals(c.getStatus()))
                .collect(Collectors.toList());

        double usedLand = activeCrops.stream()
                .mapToDouble(c -> c.getAreaPlanted() != null ? c.getAreaPlanted() : 0.0)
                .sum();

        if (usedLand + areaPlanted > farm.getLandSize()) {
            throw new IllegalArgumentException(
                    String.format("Insufficient land! You have %.1f acres available, but tried to plant %.1f acres.",
                            (farm.getLandSize() - usedLand), areaPlanted));
        }

        // 2. Strict Minimum Investment Validation
        long minRequiredInvestment = (long) (areaPlanted * MIN_INVESTMENT_PER_ACRE);
        if (investmentAmount < minRequiredInvestment) {
            throw new IllegalArgumentException(
                    String.format("Insufficient investment! Minimum ₹%d required for %.1f acres (₹%d/acre).",
                            minRequiredInvestment, areaPlanted, MIN_INVESTMENT_PER_ACRE));
        }

        // Check for active events that affect planting cost
        double costMultiplier = 1.0;
        List<com.growfund.model.RandomEvent> activeEvents = randomEventService.getActiveEvents();
        for (com.growfund.model.RandomEvent event : activeEvents) {
            if (event.getEventType().equals(com.growfund.model.RandomEvent.GOVERNMENT_SUBSIDY)) {
                costMultiplier = event.getImpactMultiplier();
                break;
            }
        }

        // Apply equipment cost reduction bonuses
        java.util.Map<String, Double> equipmentBonuses = equipmentService.calculateTotalBonuses(farmId);
        double equipmentCostMultiplier = equipmentBonuses.get("costMultiplier");
        costMultiplier *= equipmentCostMultiplier;

        Long actualCost = (long) (investmentAmount * costMultiplier);

        // Check for sufficient funds
        if (farm.getSavings() < actualCost) {
            throw new IllegalStateException("Insufficient savings to plant this crop. Required: " + actualCost);
        }

        // Deduct cost from savings
        farm.setSavings(farm.getSavings() - actualCost);
        farmRepository.save(farm);

        // Use equipment (reduce durability)
        equipmentService.useEquipment(farmId);

        Crop crop = new Crop();
        crop.setFarm(farm);
        crop.setCropType(cropType);
        crop.setAreaPlanted(areaPlanted);
        crop.setInvestmentAmount(investmentAmount);
        crop.setSeason(season);
        crop.setPlantedDate(LocalDateTime.now());
        crop.setStatus("PLANTED");

        // Calculate expected yield based on crop type and area
        Long baseYield = calculateExpectedYield(cropType, areaPlanted, investmentAmount);

        // Apply event multipliers to yield
        double yieldMultiplier = 1.0;
        for (com.growfund.model.RandomEvent event : activeEvents) {
            if (event.getEventType().equals(com.growfund.model.RandomEvent.DROUGHT) ||
                    event.getEventType().equals(com.growfund.model.RandomEvent.BONUS_RAIN) ||
                    event.getEventType().equals(com.growfund.model.RandomEvent.HEATWAVE)) {
                yieldMultiplier *= event.getImpactMultiplier();
            }
            // Pest attacks affect specific crops
            if (event.getEventType().equals(com.growfund.model.RandomEvent.PEST_ATTACK) &&
                    cropType.equals(event.getAffectedCropType())) {
                yieldMultiplier *= event.getImpactMultiplier();
            }
        }

        // Apply equipment yield bonuses
        double equipmentYieldMultiplier = equipmentBonuses.get("yieldMultiplier");
        yieldMultiplier *= equipmentYieldMultiplier;

        Long expectedYield = (long) (baseYield * yieldMultiplier);
        crop.setExpectedYield(expectedYield);

        // Set harvest date based on crop type (Testing: minutes instead of months)
        long baseGrowthTimeMinutes = getGrowthTimeInMinutes(cropType);

        // Apply weather multiplier
        WeatherService.WeatherCondition weather = weatherService.getCurrentWeather();
        double multiplier = weather.getGrowthMultiplier();
        long actualGrowthTime = (long) (baseGrowthTimeMinutes * multiplier);
        if (actualGrowthTime < 1)
            actualGrowthTime = 1; // Minimum 1 minute

        crop.setHarvestDate(LocalDateTime.now().plusMinutes(actualGrowthTime));
        crop.setWeatherImpact(weather.getDisplayName()); // Store weather at planting time

        Crop savedCrop = cropRepository.save(crop);

        // Check Achievements (e.g., First Steps)
        achievementService.checkAll(farm.getUser(), farm);

        return convertToDTO(savedCrop);
    }

    public List<CropDTO> getCropsByFarm(Long farmId) {
        return cropRepository.findByFarmId(farmId).stream()
                .sorted(java.util.Comparator.comparing(Crop::getPlantedDate,
                        java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CropDTO getCropById(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new com.growfund.exception.ResourceNotFoundException("Crop not found"));
        return convertToDTO(crop);
    }

    @Transactional
    public CropDTO harvestCrop(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new com.growfund.exception.ResourceNotFoundException("Crop not found"));

        if (!"PLANTED".equals(crop.getStatus()) && !"GROWING".equals(crop.getStatus())) {
            throw new IllegalStateException("Crop cannot be harvested in current status: " + crop.getStatus());
        }

        if (crop.getHarvestDate() != null && LocalDateTime.now().isBefore(crop.getHarvestDate())) {
            throw new IllegalStateException("Crop is not ready for harvest yet. Ready at: " + crop.getHarvestDate());
        }

        crop.setStatus("HARVESTED");
        crop.setHarvestDate(LocalDateTime.now());

        // Calculate actual yield with some randomness (80-120% of expected)
        Long expectedYield = crop.getExpectedYield();
        double variance = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
        Long actualYield = (long) (expectedYield * variance);
        crop.setActualYield(actualYield);

        // Calculate market price with event multipliers
        Long basePrice = getMarketPrice(crop.getCropType());
        double priceMultiplier = 1.0;

        // Check for market events
        List<com.growfund.model.RandomEvent> activeEvents = randomEventService.getActiveEvents();
        for (com.growfund.model.RandomEvent event : activeEvents) {
            if (event.getEventType().equals(com.growfund.model.RandomEvent.MARKET_SURGE) ||
                    event.getEventType().equals(com.growfund.model.RandomEvent.MARKET_CRASH)) {
                priceMultiplier *= event.getImpactMultiplier();
            }
        }

        Long pricePerUnit = (long) (basePrice * priceMultiplier);
        Long revenue = actualYield * pricePerUnit;
        Long profit = revenue - crop.getInvestmentAmount();

        crop.setSellingPricePerUnit(pricePerUnit);
        crop.setRevenue(revenue);
        crop.setProfit(profit);

        // Update user's coin balance from profit/revenue
        Farm farm = crop.getFarm();
        farm.setSavings(farm.getSavings() + revenue);
        farmRepository.save(farm);

        // Check Achievements
        achievementService.checkAll(crop.getFarm().getUser(), crop.getFarm());

        Crop savedCrop = cropRepository.save(crop);
        return convertToDTO(savedCrop);
    }

    private Long getMarketPrice(String cropType) {
        // Simple static prices for now
        return switch (cropType.toUpperCase()) {
            case "WHEAT" -> 20L;
            case "RICE" -> 25L;
            case "COTTON" -> 40L;
            case "SUGARCANE" -> 15L;
            case "CORN" -> 18L;
            default -> 10L;
        };
    }

    @Transactional
    public CropDTO updateCropStatus(Long cropId, String status) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new com.growfund.exception.ResourceNotFoundException("Crop not found"));

        crop.setStatus(status);
        Crop savedCrop = cropRepository.save(crop);
        return convertToDTO(savedCrop);
    }

    @Transactional
    public void deleteCrop(Long cropId) {
        if (!cropRepository.existsById(cropId)) {
            throw new com.growfund.exception.ResourceNotFoundException("Crop not found");
        }
        cropRepository.deleteById(cropId);
    }

    private Long calculateExpectedYield(String cropType, Double areaPlanted, Long investmentAmount) {
        // 3. Proportional Yield Calculation
        // Formula: Yield = BaseYield * (Investment / (StandardInvestmentPerAcre *
        // Area))

        long baseYieldPerAcre = switch (cropType.toUpperCase()) {
            case "WHEAT" -> 3000L;
            case "RICE" -> 4000L;
            case "COTTON" -> 2500L;
            case "SUGARCANE" -> 5000L;
            case "CORN" -> 3500L;
            default -> 2000L;
        };

        // Determine investment density ratio
        // Standard density is 10,000 per acre
        double requiredStandardInvestment = areaPlanted * STANDARD_INVESTMENT_PER_ACRE;

        // Ratio of actual investment to standard investment
        double investmentRatio = investmentAmount / requiredStandardInvestment;

        // Cap the boost at 2.0 (200% yield) to prevent infinite scaling
        // Since we enforce Min 5000/acre (0.5 ratio), the ratio will be between 0.5 and
        // 2.0 (capped)
        investmentRatio = Math.min(investmentRatio, 2.0);

        // Calculate final expected yield
        // BaseYieldTotal = BasePerAcre * Area
        long baseYieldTotal = (long) (baseYieldPerAcre * areaPlanted);

        return (long) (baseYieldTotal * investmentRatio);
    }

    private long getGrowthTimeInMinutes(String cropType) {
        // For testing/demo purposes, these are in minutes.
        // In production, these should be days or months.
        return switch (cropType.toUpperCase()) {
            case "WHEAT" -> 2; // 2 minutes
            case "RICE" -> 3;
            case "COTTON" -> 5;
            case "SUGARCANE" -> 4;
            case "CORN" -> 2;
            default -> 1;
        };
    }

    private CropDTO convertToDTO(Crop crop) {
        CropDTO dto = new CropDTO();
        dto.setId(crop.getId());
        dto.setCropType(crop.getCropType());
        dto.setAreaPlanted(crop.getAreaPlanted());
        dto.setInvestmentAmount(crop.getInvestmentAmount());
        dto.setExpectedYield(crop.getExpectedYield());
        dto.setActualYield(crop.getActualYield());
        dto.setSeason(crop.getSeason());
        dto.setPlantedDate(crop.getPlantedDate());
        dto.setHarvestDate(crop.getHarvestDate());
        dto.setStatus(crop.getStatus());
        dto.setWeatherImpact(crop.getWeatherImpact());
        dto.setRevenue(crop.getRevenue());
        dto.setProfit(crop.getProfit());
        dto.setSellingPricePerUnit(crop.getSellingPricePerUnit());
        return dto;
    }
}
