package com.growfund.seedtowealth.model;

public class Farm {
    private Long id;
    private String farmName;
    private Double landSize;
    private Long savings;
    private Long emergencyFund;
    private Integer cropCount;

    // Constructors
    public Farm() {
    }

    public Farm(Long id, String farmName, Double landSize, Long savings, Long emergencyFund, Integer cropCount) {
        this.id = id;
        this.farmName = farmName;
        this.landSize = landSize;
        this.savings = savings;
        this.emergencyFund = emergencyFund;
        this.cropCount = cropCount;
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
}
