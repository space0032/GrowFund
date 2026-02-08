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
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.HashMap;

import jakarta.annotation.PostConstruct;

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

    // Crop Configuration Map
    private final Map<String, CropConfig> cropConfigs = new HashMap<>();

    @PostConstruct
    public void init() {
        // Initialize Crop Configurations
        cropConfigs.put("WHEAT", new CropConfig(5000L, 0.70, 3000L, 2));
        cropConfigs.put("RICE", new CropConfig(8000L, 0.60, 4000L, 3)); // Higher water need logic -> Higher min
                                                                        // investment
        cropConfigs.put("COTTON", new CropConfig(6000L, 0.50, 2500L, 5));
        cropConfigs.put("SUGARCANE", new CropConfig(7000L, 0.40, 5000L, 4));
        cropConfigs.put("CORN", new CropConfig(5500L, 0.70, 3500L, 2));
    }

    private static class CropConfig {
        final Long minInvestmentPerAcre;
        final Double maxLandPercentage; // 0.0 to 1.0
        final Long baseYieldPerAcre;
        final int growthTimeMinutes;

        CropConfig(Long minInvestmentPerAcre, Double maxLandPercentage, Long baseYieldPerAcre, int growthTimeMinutes) {
            this.minInvestmentPerAcre = minInvestmentPerAcre;
            this.maxLandPercentage = maxLandPercentage;
            this.baseYieldPerAcre = baseYieldPerAcre;
            this.growthTimeMinutes = growthTimeMinutes;
        }
    }

    private static final Long STANDARD_INVESTMENT_PER_ACRE = 10000L;

    @Transactional
    public CropDTO plantCrop(Long farmId, String cropType, Double areaPlanted, Long investmentAmount, String season) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new com.growfund.exception.ResourceNotFoundException("Farm not found"));

        if (areaPlanted <= 0) {
            throw new IllegalArgumentException("Area planted must be greater than 0");
        }

        String normalizedCropType = cropType.toUpperCase();
        CropConfig config = cropConfigs.getOrDefault(normalizedCropType, new CropConfig(5000L, 1.0, 2000L, 1));

        // 1. Strict Acreage Validation & Limit Check
        // Calculate used land from active crops
        List<Crop> activeCrops = cropRepository.findByFarmId(farmId).stream()
                .filter(c -> "PLANTED".equals(c.getStatus()) || "GROWING".equals(c.getStatus())
                        || "READY".equals(c.getStatus()))
                .collect(Collectors.toList());

        double usedLand = activeCrops.stream()
                .mapToDouble(c -> c.getAreaPlanted() != null ? c.getAreaPlanted() : 0.0)
                .sum();

        // Check 1: Total Farm Acreage Limit
        if (usedLand + areaPlanted > farm.getLandSize()) {
            throw new IllegalArgumentException(
                    String.format("Insufficient land! You have %.1f acres available, but tried to plant %.1f acres.",
                            (farm.getLandSize() - usedLand), areaPlanted));
        }

        // Check 2: Crop-Specific Land Utilization Cap
        double currentCropTypeLand = activeCrops.stream()
                .filter(c -> c.getCropType().equalsIgnoreCase(normalizedCropType))
                .mapToDouble(c -> c.getAreaPlanted() != null ? c.getAreaPlanted() : 0.0)
                .sum();

        double maxAllowedForCrop = farm.getLandSize() * config.maxLandPercentage;
        if (currentCropTypeLand + areaPlanted > maxAllowedForCrop) {
            throw new IllegalArgumentException(
                    String.format(
                            "Crop limit exceeded! You can only use %.0f%% of your land for %s. Max allowed: %.1f acres.",
                            (config.maxLandPercentage * 100), normalizedCropType, maxAllowedForCrop));
        }

        // 2. Crop-Specific Minimum Investment Validation
        // Scale min investment based on crop type
        long minRequiredInvestment = (long) (areaPlanted * config.minInvestmentPerAcre);
        if (investmentAmount < minRequiredInvestment) {
            throw new IllegalArgumentException(
                    String.format("Insufficient investment! Minimum ₹%d required for %.1f acres of %s (₹%d/acre).",
                            minRequiredInvestment, areaPlanted, normalizedCropType, config.minInvestmentPerAcre));
        }

        // 3. Scale-Based Investment Costs (Progressive Cost Model)
        // Larger farms have higher operational costs.
        // Formula: 2% extra cost per acre above 1 acre.
        double scaleMultiplier = 1.0;
        if (farm.getLandSize() > 1.0) {
            scaleMultiplier += (farm.getLandSize() - 1.0) * 0.02;
        }

        // 4. Weather-Based Planting Cost
        WeatherService.WeatherCondition weather = weatherService.getCurrentWeather();
        double weatherCostMultiplier = weather.getPlantingCostMultiplier();

        // Calculate final required cost
        // Base cost is the investment amount the user WANTS to put in.
        // But wait, the investment amount IS the cost.
        // Interpretation: The system should probably CHARGE more for the same
        // investment value, OR reduce the effective investment.
        // Let's go with: User specifies Investment Amount (what goes into the crop).
        // The Actual Cost deducted from savings is Investment Amount * Multipliers.

        // Total Cost Multiplier
        double totalCostMultiplier = scaleMultiplier * weatherCostMultiplier;

        // Check for active events that affect planting cost
        List<com.growfund.model.RandomEvent> activeEvents = randomEventService.getActiveEvents();
        for (com.growfund.model.RandomEvent event : activeEvents) {
            if (event.getEventType().equals(com.growfund.model.RandomEvent.GOVERNMENT_SUBSIDY)) {
                totalCostMultiplier *= event.getImpactMultiplier(); // e.g. 0.8
            }
        }

        // Apply equipment cost reduction bonuses
        java.util.Map<String, Double> equipmentBonuses = equipmentService.calculateTotalBonuses(farmId);
        double equipmentCostMultiplier = equipmentBonuses.getOrDefault("costMultiplier", 1.0);
        totalCostMultiplier *= equipmentCostMultiplier;

        Long actualCost = (long) (investmentAmount * totalCostMultiplier);

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
        // Apply weather multiplier for growth time (already fetched weather above)
        // Note: Weather might change during growth, but for simplicity we use planting
        // weather impact or current?
        // Let's use current weather again to determine growth duration multiplier
        double multiplier = weather.getGrowthMultiplier();
        long actualGrowthTime = (long) (baseGrowthTimeMinutes * multiplier);
        if (actualGrowthTime < 1)
            actualGrowthTime = 1; // Minimum 1 minute

        crop.setHarvestDate(LocalDateTime.now().plusMinutes(actualGrowthTime));
        crop.setWeatherImpact(weather.getDisplayName()); // Store weather at planting time

        if (totalCostMultiplier > 1.05) {
            // If cost was significantly higher due to weather/scale, maybe log it or note
            // it?
            // For now, just implicit.
        }

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
        // 3. Proportional Yield Calculation with Diminishing Returns

        CropConfig config = cropConfigs.getOrDefault(cropType.toUpperCase(), new CropConfig(5000L, 1.0, 2000L, 1));
        long baseYieldPerAcre = config.baseYieldPerAcre;

        // Determine investment density ratio
        // Standard density is 10,000 per acre
        double requiredStandardInvestment = areaPlanted * STANDARD_INVESTMENT_PER_ACRE;

        // Ratio of actual investment to standard investment
        double investmentRatio = investmentAmount / requiredStandardInvestment;

        // Diminishing Returns Logic:
        // Linear up to 1.0 ratio.
        // Square root scaling beyond 1.0 significantly dampens returns for
        // over-investment.
        double effectiveRatio;
        if (investmentRatio <= 1.0) {
            effectiveRatio = investmentRatio;
        } else {
            // e.g. if ratio is 2.0, effective is 1.0 + sqrt(1.0) * 0.5 = 1.5?
            // Let's use a log scale or simple dampening.
            // effective = 1.0 + ln(ratio) * 0.5
            effectiveRatio = 1.0 + Math.log(investmentRatio) * 0.6; // Logarithmic growth after 1.0
        }

        // Cap the effective ratio to prevent exploits (e.g. max 2.5x yield)
        effectiveRatio = Math.min(effectiveRatio, 2.5);

        // Calculate final expected yield
        // BaseYieldTotal = BasePerAcre * Area
        long baseYieldTotal = (long) (baseYieldPerAcre * areaPlanted);

        // Add minimal soil fertility factor (randomized for now between 0.9 and 1.1)
        double soilFertility = 0.9 + (random.nextDouble() * 0.2);

        return (long) (baseYieldTotal * effectiveRatio * soilFertility);
    }

    private long getGrowthTimeInMinutes(String cropType) {
        CropConfig config = cropConfigs.getOrDefault(cropType.toUpperCase(), new CropConfig(5000L, 1.0, 2000L, 1));
        return config.growthTimeMinutes;
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
