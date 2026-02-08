package com.growfund.seedtowealth.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "equipment")
public class Equipment {

    @PrimaryKey
    private Long id;

    private String name;
    private String description;

    @ColumnInfo(name = "equipment_type")
    private String equipmentType; // IRRIGATION, FERTILIZER, TOOLS, SEEDS

    private String tier; // BASIC, ADVANCED, PREMIUM
    private Long cost;

    @ColumnInfo(name = "yield_bonus")
    private Double yieldBonus;

    @ColumnInfo(name = "cost_reduction")
    private Double costReduction;

    @ColumnInfo(name = "max_durability")
    private Integer maxDurability;

    private String icon;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    // Equipment Types
    public static final String IRRIGATION = "IRRIGATION";
    public static final String FERTILIZER = "FERTILIZER";
    public static final String TOOLS = "TOOLS";
    public static final String SEEDS = "SEEDS";

    // Tiers
    public static final String BASIC = "BASIC";
    public static final String ADVANCED = "ADVANCED";
    public static final String PREMIUM = "PREMIUM";

    // Constructors
    public Equipment() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Double getYieldBonus() {
        return yieldBonus;
    }

    public void setYieldBonus(Double yieldBonus) {
        this.yieldBonus = yieldBonus;
    }

    public Double getCostReduction() {
        return costReduction;
    }

    public void setCostReduction(Double costReduction) {
        this.costReduction = costReduction;
    }

    public Integer getMaxDurability() {
        return maxDurability;
    }

    public void setMaxDurability(Integer maxDurability) {
        this.maxDurability = maxDurability;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Helper methods
    public String getDisplayType() {
        return switch (equipmentType) {
            case IRRIGATION -> "Irrigation System";
            case FERTILIZER -> "Fertilizer";
            case TOOLS -> "Tools";
            case SEEDS -> "Seeds";
            default -> "Equipment";
        };
    }

    public String getTierDisplay() {
        return switch (tier) {
            case BASIC -> "Basic";
            case ADVANCED -> "Advanced";
            case PREMIUM -> "Premium";
            default -> tier;
        };
    }

    public boolean hasYieldBonus() {
        return yieldBonus != null && yieldBonus > 0;
    }

    public boolean hasCostReduction() {
        return costReduction != null && costReduction > 0;
    }

    public String getBonusText() {
        StringBuilder bonus = new StringBuilder();
        if (hasYieldBonus()) {
            bonus.append("+").append((int) (yieldBonus * 100)).append("% Yield");
        }
        if (hasCostReduction()) {
            if (bonus.length() > 0)
                bonus.append(", ");
            bonus.append("-").append((int) (costReduction * 100)).append("% Cost");
        }
        return bonus.toString();
    }

    public String getYieldBonusText() {
        if (hasYieldBonus()) {
            return "+" + (int) (yieldBonus * 100) + "% Yield";
        }
        return "";
    }

    public String getCostReductionText() {
        if (hasCostReduction()) {
            return "-" + (int) (costReduction * 100) + "% Cost";
        }
        return "";
    }
}
