package com.growfund.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MarketService {

    private final Map<String, Double> basePrices = new HashMap<>();
    private final Random random = new Random();

    public MarketService() {
        // Base prices per unit (roughly based on yield)
        basePrices.put("WHEAT", 40.0);
        basePrices.put("RICE", 50.0);
        basePrices.put("COTTON", 80.0);
        basePrices.put("SUGARCANE", 30.0);
        basePrices.put("CORN", 35.0);
    }

    /**
     * Returns a map of CropType -> CurrentSellingPrice
     * Prices fluctuate by +/- 20%
     */
    public Map<String, Double> getMarketTrends() {
        Map<String, Double> currentPrices = new HashMap<>();

        for (Map.Entry<String, Double> entry : basePrices.entrySet()) {
            double base = entry.getValue();
            // Random fluctuation between -20% and +20%
            double fluctuation = -0.20 + (0.40 * random.nextDouble());
            double currentPrice = base * (1 + fluctuation);

            // Round to 2 decimal places
            currentPrice = Math.round(currentPrice * 100.0) / 100.0;
            currentPrices.put(entry.getKey(), currentPrice);
        }
        return currentPrices;
    }

    public double getBasePrice(String cropType) {
        return basePrices.getOrDefault(cropType, 0.0);
    }
}
