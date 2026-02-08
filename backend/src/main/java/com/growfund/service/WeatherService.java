package com.growfund.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class WeatherService {

    public enum WeatherCondition {
        SUNNY("Sunny", 1.0, 1.0),
        RAINY("Rainy", 0.8, 1.2), // 20% faster growth (0.8x duration), 20% higher planting cost
        DROUGHT("Drought", 1.5, 1.1); // 50% slower growth (1.5x duration), 10% higher planting cost

        private final String displayName;
        private final double growthMultiplier;
        private final double plantingCostMultiplier;

        WeatherCondition(String displayName, double growthMultiplier, double plantingCostMultiplier) {
            this.displayName = displayName;
            this.growthMultiplier = growthMultiplier;
            this.plantingCostMultiplier = plantingCostMultiplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public double getGrowthMultiplier() {
            return growthMultiplier;
        }

        public double getPlantingCostMultiplier() {
            return plantingCostMultiplier;
        }
    }

    private WeatherCondition currentWeather = WeatherCondition.SUNNY;
    private final Random random = new Random();

    /**
     * Randomly changes the weather.
     * In a real app, this might be scheduled or based on real weather API.
     * For now, we'll randomize it on clear request or keep it session based.
     * Let's make it random but stable for a few calls?
     * Simpler: Randomize on every request to 'refresh' endpoint, or just return
     * random.
     * To make it testable, let's keep it stored and have a method to refresh it.
     */
    public void refreshWeather() {
        int pick = random.nextInt(WeatherCondition.values().length);
        currentWeather = WeatherCondition.values()[pick];
    }

    public WeatherCondition getCurrentWeather() {
        // Auto-refresh logic could go here, but let's keep it simple:
        // effectively random for this demo, or fixed.
        // Let's randomize if it's been a while?
        // For demo purposes, let's randomize it every time we ask,
        // creating dynamic fun.
        refreshWeather();
        return currentWeather;
    }
}
