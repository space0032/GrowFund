package com.growfund.seedtowealth.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "crops")
public class Crop {
    @PrimaryKey
    private Long id;

    @ColumnInfo(name = "farm_id")
    private Long farmId; // Foreign key reference

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

    public Long getFarmId() {
        return farmId;
    }

    public void setFarmId(Long farmId) {
        this.farmId = farmId;
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

    public long getTimeRemainingInMillis() {
        if (harvestDate == null)
            return 0;
        try {
            // Backend sends ISO format roughly: "2023-10-27T10:00:00"
            // We need to parse this.
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                java.time.LocalDateTime harvest = java.time.LocalDateTime.parse(harvestDate);
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                long diff = java.time.Duration.between(now, harvest).toMillis();
                return diff > 0 ? diff : 0;
            } else {
                // Fallback for older devices if needed, but minSdk 24 usually implies
                // desugaring or we can use SimpleDateFormat
                // For simplicity in this demo environment with Java 17, we assume java.time
                // works or we use a basic string parse if needed.
                // However, let's stick to standard Java time which is robust.
                // If it crashes on older devices without desugaring, we'd need ThreeTenABP.
                // Given the environment, let's verify if we need to support < O.
                // MinSdk 24 supports some java.time but full support is 26.
                // Let's use SimpleDateFormat for maximum compatibility.
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                java.util.Date harvest = sdf.parse(harvestDate);
                long diff = harvest.getTime() - System.currentTimeMillis();
                return diff > 0 ? diff : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
