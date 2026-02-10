package com.growfund.seedtowealth.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;

@Entity(tableName = "farms")
public class Farm {
    @PrimaryKey
    private Long id;
    private String farmName;
    private Double landSize;
    private Double availableLand; // Land not currently planted
    private Long savings;
    private Long emergencyFund;
    private Integer cropCount;
    private Long expansionCost; // Cost for next expansion

    // Constructors
    public Farm() {
    }

    @Ignore
    public Farm(Long id, String farmName, Double landSize, Double availableLand, Long savings, Long emergencyFund,
            Integer cropCount, Long expansionCost) {
        this.id = id;
        this.farmName = farmName;
        this.landSize = landSize;
        this.availableLand = availableLand;
        this.savings = savings;
        this.emergencyFund = emergencyFund;
        this.cropCount = cropCount;
        this.expansionCost = expansionCost;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public Double getLandSize() {
        return landSize;
    }

    public void setLandSize(Double landSize) {
        this.landSize = landSize;
    }

    public Double getAvailableLand() {
        return availableLand;
    }

    public void setAvailableLand(Double availableLand) {
        this.availableLand = availableLand;
    }

    public Long getSavings() {
        return savings;
    }

    public void setSavings(Long savings) {
        this.savings = savings;
    }

    public Long getEmergencyFund() {
        return emergencyFund;
    }

    public void setEmergencyFund(Long emergencyFund) {
        this.emergencyFund = emergencyFund;
    }

    public Integer getCropCount() {
        return cropCount;
    }

    public void setCropCount(Integer cropCount) {
        this.cropCount = cropCount;
    }

    public Long getExpansionCost() {
        return expansionCost;
    }

    public void setExpansionCost(Long expansionCost) {
        this.expansionCost = expansionCost;
    }
}
