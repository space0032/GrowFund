package com.growfund.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MarketService {

    private final Map<String, PriceRange> priceRanges = new HashMap<>();
    private final Random random = new Random();

    public MarketService() {
        // Initialize Price Ranges according to realistic economics
        // Wheat: ₹15-25
        priceRanges.put("WHEAT", new PriceRange(15.0, 25.0));
        // Rice: ₹20-30
        priceRanges.put("RICE", new PriceRange(20.0, 30.0));
        // Cotton: ₹40-50
        priceRanges.put("COTTON", new PriceRange(40.0, 50.0));
        // Sugarcane: ₹250-300 per ton usually, so per kg is low ~0.25?
        // Game balance: Let's assume per unit is a "bundle" or make it ₹3-5 and high
        // yield.
        // In CropService I set yield 25000-35000.
        // If price is 20, Revenue = 25000 * 20 = 500,000. Too high.
        // If Wheat Yield 2000 * 20 = 40,000.
        // So Sugarcane price matches yield. If yield is 10x wheat, price should be
        // 1/10th.
        // Sugarcane Price: ₹1.5 - ₹2.5.
        priceRanges.put("SUGARCANE", new PriceRange(1.5, 2.5));

        // Corn: ₹15-22
        priceRanges.put("CORN", new PriceRange(15.0, 22.0));
    }

    private record PriceRange(Double min, Double max) {
    }

    /**
     * Get the current market price for a crop.
     * Simulates demand/supply fluctuation.
     */
    public Double getMarketPrice(String cropType) {
        PriceRange range = priceRanges.get(cropType.toUpperCase());
        if (range == null) {
            return 10.0; // Default fallback
        }

        // Return a random value within the range
        // In a real app, this would be stored/cached for the day to avoid fluctuation
        // per click
        // For now, we calculate on the fly as requested
        return range.min + (random.nextDouble() * (range.max - range.min));
    }

    /**
     * Returns a map of CropType -> CurrentSellingPrice
     * Used for UI display of trends
     */
    public Map<String, Double> getMarketTrends() {
        Map<String, Double> currentPrices = new HashMap<>();

        for (String crop : priceRanges.keySet()) {
            Double price = getMarketPrice(crop);
            // Round to 2 decimal places
            price = Math.round(price * 100.0) / 100.0;
            currentPrices.put(crop, price);
        }
        return currentPrices;
    }

    public double getBasePrice(String cropType) {
        PriceRange range = priceRanges.get(cropType.toUpperCase());
        if (range == null)
            return 10.0;
        return (range.min + range.max) / 2.0; // Return average as base
    }
}
