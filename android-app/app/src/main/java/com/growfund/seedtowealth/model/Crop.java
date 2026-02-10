package com.growfund.seedtowealth.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Index;

@Entity(tableName = "crops", indices = {
        @Index(value = "farm_id"),
        @Index(value = "status")
})
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
            // Fallback / Standard Java parsing
            // Try ISO format first, then simple format
            try {
                // Start of epoch
                long nowMillis = System.currentTimeMillis();
                long harvestMillis = 0;

                // If string contains T, likely ISO
                if (harvestDate.contains("T")) {
                    java.time.LocalDateTime harvest = java.time.LocalDateTime.parse(harvestDate);
                    java.time.ZoneId zoneId = java.time.ZoneId.systemDefault();
                    harvestMillis = harvest.atZone(zoneId).toInstant().toEpochMilli();
                } else {
                    // Simple Format
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date date = sdf.parse(harvestDate);
                    if (date != null)
                        harvestMillis = date.getTime();
                }

                long diff = harvestMillis - nowMillis;
                return diff > 0 ? diff : 0;

            } catch (Exception e2) {
                // Last resort fallbacks
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
