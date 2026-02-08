package com.growfund.seedtowealth.model;

import java.util.Map;

public class AnalyticsData {
    private Long totalPortfolioValue;
    private Long savings;
    private Long investmentValue;
    private Long cropValue;
    private Long equipmentValue;
    private Map<String, Long> investmentDistribution;
    private Double historicalRoi;
    private Double projectedRoi;

    public Long getTotalPortfolioValue() {
        return totalPortfolioValue;
    }

    public Long getSavings() {
        return savings;
    }

    public Long getInvestmentValue() {
        return investmentValue;
    }

    public Long getCropValue() {
        return cropValue;
    }

    public Long getEquipmentValue() {
        return equipmentValue;
    }

    public Map<String, Long> getInvestmentDistribution() {
        return investmentDistribution;
    }

    public Double getHistoricalRoi() {
        return historicalRoi;
    }

    public Double getProjectedRoi() {
        return projectedRoi;
    }
}
