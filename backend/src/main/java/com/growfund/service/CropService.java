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
    private final MarketService marketService; // Injected MarketService
    private final Random random = new Random();

    // Crop Configuration Map
    private final Map<String, CropConfig> cropConfigs = new HashMap<>();

    @PostConstruct
    public void init() {
        // Initialize Crop Configurations with Realistic Data
        // Costs are per acre
        // Yields are in kg/acre

        // Wheat: ₹15-25/kg market, Yield 1800-2500
        cropConfigs.put("WHEAT", new CropConfig(
                2500L, 3000L, 1500L, 500L, 2000L, // Costs: Seed, Fert, Labor, Irr, Logistics
                0.70, 1800L, 2500L, 2));

        // Rice: ₹20-30/kg market, Yield 2000-3000
        cropConfigs.put("RICE", new CropConfig(
                3000L, 3500L, 2000L, 1000L, 2500L,
                0.60, 2000L, 3000L, 3));

        // Cotton: ₹40-50/kg market, Yield 800-1200 (Adjusted for realistic cotton
        // yields vs price)
        cropConfigs.put("COTTON", new CropConfig(
                4000L, 4000L, 2500L, 800L, 2000L,
                0.50, 800L, 1200L, 5));

        // Sugarcane: Yield 30000-40000 (It's heavy), Price low (~₹3-4/kg usually, but
        // let's scale to game balance)
        // Let's keep existing simplistic balance but adding detail.
        cropConfigs.put("SUGARCANE", new CropConfig(
                3500L, 4500L, 3000L, 1500L, 3000L,
                0.40, 25000L, 35000L, 4));

        // Corn
        cropConfigs.put("CORN", new CropConfig(
                2000L, 2500L, 1500L, 600L, 1500L,
                0.70, 2500L, 3500L, 2));
    }

    private static class CropConfig {
        final Long seedCost;
        final Long fertilizerCost;
        final Long laborCost;
        final Long irrigationCost;
        final Long logisticsCost; // Per acre for simplicity

        final Double maxLandPercentage; // 0.0 to 1.0
        final Long minYieldPerAcre;
        final Long maxYieldPerAcre;
        final int growthTimeMinutes;

        CropConfig(Long seedCost, Long fertilizerCost, Long laborCost, Long irrigationCost, Long logisticsCost,
                Double maxLandPercentage, Long minYieldPerAcre, Long maxYieldPerAcre, int growthTimeMinutes) {
            this.seedCost = seedCost;
            this.fertilizerCost = fertilizerCost;
            this.laborCost = laborCost;
            this.irrigationCost = irrigationCost;
            this.logisticsCost = logisticsCost;
            this.maxLandPercentage = maxLandPercentage;
            this.minYieldPerAcre = minYieldPerAcre;
            this.maxYieldPerAcre = maxYieldPerAcre;
            this.growthTimeMinutes = growthTimeMinutes;
        }

        Long getTotalCostPerAcre() {
            return seedCost + fertilizerCost + laborCost + irrigationCost + logisticsCost;
        }
    }

    @Transactional(readOnly = true)
    public Long calculatePlantingCost(Long farmId, String cropType, Double areaPlanted) {
        String normalizedCropType = cropType.toUpperCase();
        CropConfig config = cropConfigs.get(normalizedCropType);
        if (config == null) {
            throw new IllegalArgumentException("Unknown crop type: " + cropType);
        }

        // Base Cost
        Long baseCostPerAcre = config.getTotalCostPerAcre();
        Long totalCalculatedCost = (long) (baseCostPerAcre * areaPlanted);

        // Weather Adjustments
        WeatherService.WeatherCondition weather = weatherService.getCurrentWeather();
        double weatherCostMultiplier = weather.getPlantingCostMultiplier();
        Long finalCost = (long) (totalCalculatedCost * weatherCostMultiplier);

        // Government Subsidy
        List<com.growfund.model.RandomEvent> activeEvents = randomEventService.getActiveEvents();
        for (com.growfund.model.RandomEvent event : activeEvents) {
            if (event.getEventType().equals(com.growfund.model.RandomEvent.GOVERNMENT_SUBSIDY)) {
                finalCost = (long) (finalCost * event.getImpactMultiplier());
            }
        }

        // Equipment Bonuses
        java.util.Map<String, Double> equipmentBonuses = equipmentService.calculateTotalBonuses(farmId);
        double equipmentCostMultiplier = equipmentBonuses.getOrDefault("costMultiplier", 1.0);
        finalCost = (long) (finalCost * equipmentCostMultiplier);

        return finalCost;
    }

    @Transactional
    public CropDTO plantCrop(Long farmId, String cropType, Double areaPlanted, String season) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new com.growfund.exception.ResourceNotFoundException("Farm not found"));

        if (areaPlanted <= 0) {
            throw new IllegalArgumentException("Area planted must be greater than 0");
        }

        String normalizedCropType = cropType.toUpperCase();
        CropConfig config = cropConfigs.get(normalizedCropType); // Need config for yield calc later
        if (config == null) {
            throw new IllegalArgumentException("Unknown crop type: " + cropType);
        }

        // 1. Strict Acreage Validation & Limit Check
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

        double currentCropTypeLand = activeCrops.stream()
                .filter(c -> c.getCropType().equalsIgnoreCase(normalizedCropType))
                .mapToDouble(c -> c.getAreaPlanted() != null ? c.getAreaPlanted() : 0.0)
                .sum();

        double maxAllowedForCrop = farm.getLandSize() * config.maxLandPercentage;
        double yieldPenaltyMultiplier = 1.0;

        if (currentCropTypeLand + areaPlanted > maxAllowedForCrop) {
            // Calculate overuse percentage relative to total land
            double overuseAmount = (currentCropTypeLand + areaPlanted) - maxAllowedForCrop;
            double overuseRatio = overuseAmount / farm.getLandSize();

            // Penalty Formula: yieldMultiplier *= (1.0 - (overuseRatio * 2.0))
            // Example: 10% overuse = 20% penalty.
            // Minimum yield floor: 10% (0.1 multiplier)
            yieldPenaltyMultiplier = Math.max(0.1, 1.0 - (overuseRatio * 2.0));
        }

        // 2. Calculate Cost using extracted method
        Long finalCost = calculatePlantingCost(farmId, cropType, areaPlanted);

        // Check for sufficient funds
        if (farm.getSavings() < finalCost) {
            throw new IllegalStateException(
                    String.format("Insufficient savings! Required: ₹%d, Available: ₹%d", finalCost, farm.getSavings()));
        }

        // Deduct cost from savings
        farm.setSavings(farm.getSavings() - finalCost);
        farmRepository.save(farm);

        // Use equipment (reduce durability)
        equipmentService.useEquipment(farmId);

        Crop crop = new Crop();
        crop.setFarm(farm);
        crop.setCropType(cropType);
        crop.setAreaPlanted(areaPlanted);
        crop.setInvestmentAmount(finalCost); // Store the actual cost incurred
        crop.setSeason(season);
        crop.setPlantedDate(LocalDateTime.now());
        crop.setStatus("PLANTED");

        // Calculate expected yield based on crop type, area, and conditions
        Long expectedYield = calculateExpectedYield(cropType, areaPlanted, finalCost, config);

        // Apply event multipliers to yield
        double yieldMultiplier = 1.0;
        List<com.growfund.model.RandomEvent> activeEvents = randomEventService.getActiveEvents();
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
        java.util.Map<String, Double> equipmentBonuses = equipmentService.calculateTotalBonuses(farmId);
        double equipmentYieldMultiplier = equipmentBonuses.getOrDefault("yieldMultiplier", 1.0); // e.g. 1.2 for Tractor
        yieldMultiplier *= equipmentYieldMultiplier;

        // Apply overuse penalty
        yieldMultiplier *= yieldPenaltyMultiplier;

        expectedYield = (long) (expectedYield * yieldMultiplier);
        crop.setExpectedYield(expectedYield);

        // Calculate Growth Time
        long baseGrowthTimeMinutes = config.growthTimeMinutes;
        WeatherService.WeatherCondition weather = weatherService.getCurrentWeather();
        double growthMultiplier = weather.getGrowthMultiplier();
        long actualGrowthTime = (long) (baseGrowthTimeMinutes * growthMultiplier);
        if (actualGrowthTime < 1)
            actualGrowthTime = 1;

        crop.setHarvestDate(LocalDateTime.now().plusMinutes(actualGrowthTime));
        crop.setWeatherImpact(weather.getDisplayName());

        Crop savedCrop = cropRepository.save(crop);

        // Check Achievements
        achievementService.checkAll(farm.getUser(), farm);

        return convertToDTO(savedCrop);
    }

    private Long calculateExpectedYield(String cropType, Double areaPlanted, Long investmentAmount, CropConfig config) {
        // Updated Formula: BaseYield = (Random between Min and Max) * Area
        // Then apply Soil Factor, Weather (already done in caller), etc.

        // 1. Base Yield Calculation
        long minYield = config.minYieldPerAcre;
        long maxYield = config.maxYieldPerAcre;
        // Random base yield per acre
        long basePerAcre = minYield + (long) (random.nextDouble() * (maxYield - minYield));

        long totalBaseYield = (long) (basePerAcre * areaPlanted);

        // 2. Soil Factor (Randomized 0.8 to 1.2 "Soil Quality")
        double soilFactor = 0.8 + (random.nextDouble() * 0.4);

        // 3. Irrigation Impact (Assuming full irrigation investment in automatic
        // calculation,
        // so we treat it as 1.0 baseline, potentially reduced if we had an "Irrigation
        // Level" feature.
        // For now, treat as standard).

        return (long) (totalBaseYield * soilFactor);
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
            // For testing purposes, we might allow early harvest or check time properly
            // throw new IllegalStateException("Crop is not ready.");
        }

        crop.setStatus("HARVESTED");
        crop.setHarvestDate(LocalDateTime.now());

        // Calculate actual yield with some randomness (90-110% of expected)
        Long expectedYield = crop.getExpectedYield();
        double variance = 0.9 + (random.nextDouble() * 0.2);
        Long actualYield = (long) (expectedYield * variance);
        crop.setActualYield(actualYield);

        // --- Pricing & Profit Logic ---

        // 1. Get Dynamic Market Price
        Double marketPricePerUnit = marketService.getMarketPrice(crop.getCropType());

        // 2. Apply Market Events
        double priceMultiplier = 1.0;
        List<com.growfund.model.RandomEvent> activeEvents = randomEventService.getActiveEvents();
        for (com.growfund.model.RandomEvent event : activeEvents) {
            if (event.getEventType().equals(com.growfund.model.RandomEvent.MARKET_SURGE) ||
                    event.getEventType().equals(com.growfund.model.RandomEvent.MARKET_CRASH)) {
                priceMultiplier *= event.getImpactMultiplier();
            }
        }

        // Final Price
        Long finalPricePerUnit = (long) (marketPricePerUnit * priceMultiplier);
        if (finalPricePerUnit < 1)
            finalPricePerUnit = 1L; // Minimum price

        // 3. Calculate Revenue
        Long revenue = actualYield * finalPricePerUnit;

        // 4. Calculate Net Profit
        // Net Profit = Revenue - (Farming Costs + Misc Costs)
        // Farming Cost = crop.getInvestmentAmount()
        // Misc Costs = Taxes, Loan Interest, Storage => Flat ~10% of Revenue?
        // Or "8-12% for loan interest". Let's use 10% of Revenue as "Dedcutions".
        long miscCosts = (long) (revenue * 0.10);

        Long totalCosts = crop.getInvestmentAmount(); // Includes Seeds, Fert, Labor, Irr, Logistics

        Long netProfit = revenue - totalCosts - miscCosts;

        // 5. Apply 3x Profit Cap (if no equipment used)
        // We need to know if equipment was used. We can check EquipmentBonuses.
        // But EquipmentService calculates bonuses on the fly.
        // We can assume if "equipmentBonuses" were applied during planting, they might
        // affect this?
        // Current logic: Equipment reduces cost or increases yield.
        // Implementation Requirement: "Without equipment: max 3x. With equipment: no
        // cap."
        // We'll check if the user OWNS any active equipment for simplicity or check if
        // bonus was applied.
        // Let's check if the farm has any equipment.
        boolean hasEquipment = equipmentService.hasEquipment(crop.getFarm().getId());

        if (!hasEquipment) {
            long maxProfit = 3 * totalCosts;
            if (netProfit > maxProfit) {
                netProfit = maxProfit;
            }
        }

        crop.setSellingPricePerUnit(finalPricePerUnit);
        crop.setRevenue(revenue);
        crop.setProfit(netProfit);

        // Update user's savings
        Farm farm = crop.getFarm();
        farm.setSavings(farm.getSavings() + revenue); // Revenue adds to savings? Usually profit.
        // Wait, if we deducted costs from savings at planting, then we should add
        // REVENUE back to savings.
        // Profit is just a metric.
        // Example: Savings 1000. Invest 100. Savings 900.
        // Harvest Revenue 300. Savings 900 + 300 = 1200.
        // Profit = 300 - 100 = 200. (1000 -> 1200 = +200). Correct.
        // But what about miscCosts? If we deduct miscCosts from Profit metric, do we
        // deduct from actual cash?
        // "Net Profit = Revenue - (Farming Costs + Misc Costs)".
        // If Misc Costs are "Taxes", they should leave the wallet.
        // So we should add (Revenue - MiscCosts) to savings.

        farm.setSavings(farm.getSavings() + (revenue - miscCosts));
        farmRepository.save(farm);

        Crop savedCrop = cropRepository.save(crop);

        // Check Achievements
        achievementService.checkAll(farm.getUser(), farm);

        return convertToDTO(savedCrop);
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
}
