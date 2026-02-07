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
    private final Random random = new Random();

    @Transactional
    public CropDTO plantCrop(Long farmId, String cropType, Double areaPlanted, Long investmentAmount, String season) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        Crop crop = new Crop();
        crop.setFarm(farm);
        crop.setCropType(cropType);
        crop.setAreaPlanted(areaPlanted);
        crop.setInvestmentAmount(investmentAmount);
        crop.setSeason(season);
        crop.setPlantedDate(LocalDateTime.now());
        crop.setStatus("PLANTED");

        // Calculate expected yield based on crop type and area
        crop.setExpectedYield(calculateExpectedYield(cropType, areaPlanted, investmentAmount));

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
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CropDTO getCropById(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
        return convertToDTO(crop);
    }

    @Transactional
    public CropDTO harvestCrop(Long cropId) {
        Crop crop = cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        if (!"PLANTED".equals(crop.getStatus()) && !"GROWING".equals(crop.getStatus())) {
            throw new RuntimeException("Crop cannot be harvested in current status: " + crop.getStatus());
        }

        if (crop.getHarvestDate() != null && LocalDateTime.now().isBefore(crop.getHarvestDate())) {
            throw new RuntimeException("Crop is not ready for harvest yet. Ready at: " + crop.getHarvestDate());
        }

        crop.setStatus("HARVESTED");
        crop.setHarvestDate(LocalDateTime.now());

        // Calculate actual yield with some randomness (80-120% of expected)
        Long expectedYield = crop.getExpectedYield();
        double variance = 0.8 + (random.nextDouble() * 0.4); // 0.8 to 1.2
        Long actualYield = (long) (expectedYield * variance);
        crop.setActualYield(actualYield);

        // Calculate functionality details
        Long pricePerUnit = getMarketPrice(crop.getCropType());
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
                .orElseThrow(() -> new RuntimeException("Crop not found"));

        crop.setStatus(status);
        Crop savedCrop = cropRepository.save(crop);
        return convertToDTO(savedCrop);
    }

    @Transactional
    public void deleteCrop(Long cropId) {
        if (!cropRepository.existsById(cropId)) {
            throw new RuntimeException("Crop not found");
        }
        cropRepository.deleteById(cropId);
    }

    private Long calculateExpectedYield(String cropType, Double areaPlanted, Long investmentAmount) {
        // Simple calculation: base yield per acre * area * investment factor
        long baseYieldPerAcre = switch (cropType.toUpperCase()) {
            case "WHEAT" -> 3000L;
            case "RICE" -> 4000L;
            case "COTTON" -> 2500L;
            case "SUGARCANE" -> 5000L;
            case "CORN" -> 3500L;
            default -> 2000L;
        };

        double investmentFactor = 1.0 + (investmentAmount / 10000.0 * 0.1); // 10% boost per 10k investment
        return (long) (baseYieldPerAcre * areaPlanted * investmentFactor);
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
