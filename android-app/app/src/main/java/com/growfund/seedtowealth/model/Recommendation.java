package com.growfund.seedtowealth.model;

public class Recommendation {
    private String title;
    private String description;
    private String type; // SAFE, GROWTH, HIGH_YIELD, STRATEGY
    private String category; // CROP, INVESTMENT, EQUIPMENT, TIP
    private String riskLevel; // LOW, MEDIUM, HIGH
    private Long estimatedCost;
    private Double estimatedReturn;
    private String iconName;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public Long getEstimatedCost() {
        return estimatedCost;
    }

    public Double getEstimatedReturn() {
        return estimatedReturn;
    }

    public String getIconName() {
        return iconName;
    }
}
