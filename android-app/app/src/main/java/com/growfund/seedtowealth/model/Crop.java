package com.growfund.seedtowealth.model;

public class Crop {
    private Long id;
    private String cropType;
    private Double areaPlanted;
    private Long investmentAmount;
    private Long expectedYield;
    private Long actualYield;
    private String season;
    private String plantedDate;
    private String harvestDate;
    private String status;
    private String weatherImpact;
    private Long revenue;
    private Long profit;
    private Long sellingPricePerUnit;

    // Constructors
    public Crop() {
    }

    // Getters and Setters
    public Long getRevenue() {
        return revenue;
    }

    public void setRevenue(Long revenue) {
        this.revenue = revenue;
    }

    public Long getProfit() {
        return profit;
    }

    public void setProfit(Long profit) {
        this.profit = profit;
    }

    public Long getSellingPricePerUnit() {
        return sellingPricePerUnit;
    }

    public void setSellingPricePerUnit(Long sellingPricePerUnit) {
        this.sellingPricePerUnit = sellingPricePerUnit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCropType() {
        return cropType;
    }

    public void setCropType(String cropType) {
        this.cropType = cropType;
    }

    public Double getAreaPlanted() {
        return areaPlanted;
    }

    public void setAreaPlanted(Double areaPlanted) {
        this.areaPlanted = areaPlanted;
    }

    public Long getInvestmentAmount() {
        return investmentAmount;
    }

    public void setInvestmentAmount(Long investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public Long getExpectedYield() {
        return expectedYield;
    }

    public void setExpectedYield(Long expectedYield) {
        this.expectedYield = expectedYield;
    }

    public Long getActualYield() {
        return actualYield;
    }

    public void setActualYield(Long actualYield) {
        this.actualYield = actualYield;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getPlantedDate() {
        return plantedDate;
    }

    public void setPlantedDate(String plantedDate) {
        this.plantedDate = plantedDate;
    }

    public String getHarvestDate() {
        return harvestDate;
    }

    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWeatherImpact() {
        return weatherImpact;
    }

    public void setWeatherImpact(String weatherImpact) {
        this.weatherImpact = weatherImpact;
    }
}
