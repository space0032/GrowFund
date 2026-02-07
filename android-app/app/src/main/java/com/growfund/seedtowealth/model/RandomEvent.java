package com.growfund.seedtowealth.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "random_events")
public class RandomEvent {

    @PrimaryKey
    private Long id;

    private String eventType; // DROUGHT, PEST_ATTACK, BONUS_RAIN, MARKET_SURGE, etc.
    private String severity; // LOW, MEDIUM, HIGH
    private String startTime; // ISO 8601 format
    private String endTime;
    private boolean active;
    private String description;
    private Double impactMultiplier;
    private String affectedCropType;
    private String createdAt;

    // Event Types
    public static final String DROUGHT = "DROUGHT";
    public static final String PEST_ATTACK = "PEST_ATTACK";
    public static final String BONUS_RAIN = "BONUS_RAIN";
    public static final String MARKET_SURGE = "MARKET_SURGE";
    public static final String MARKET_CRASH = "MARKET_CRASH";
    public static final String HEATWAVE = "HEATWAVE";
    public static final String GOVERNMENT_SUBSIDY = "GOVERNMENT_SUBSIDY";

    // Severity Levels
    public static final String LOW = "LOW";
    public static final String MEDIUM = "MEDIUM";
    public static final String HIGH = "HIGH";

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getImpactMultiplier() {
        return impactMultiplier;
    }

    public void setImpactMultiplier(Double impactMultiplier) {
        this.impactMultiplier = impactMultiplier;
    }

    public String getAffectedCropType() {
        return affectedCropType;
    }

    public void setAffectedCropType(String affectedCropType) {
        this.affectedCropType = affectedCropType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to get event icon
    public String getEventIcon() {
        switch (eventType) {
            case DROUGHT:
                return "☀️";
            case PEST_ATTACK:
                return "🐛";
            case BONUS_RAIN:
                return "🌧️";
            case MARKET_SURGE:
                return "📈";
            case MARKET_CRASH:
                return "📉";
            case HEATWAVE:
                return "🔥";
            case GOVERNMENT_SUBSIDY:
                return "🏛️";
            default:
                return "⚠️";
        }
    }

    // Helper method to determine if event is positive
    public boolean isPositiveEvent() {
        return eventType.equals(BONUS_RAIN) ||
                eventType.equals(MARKET_SURGE) ||
                eventType.equals(GOVERNMENT_SUBSIDY);
    }
}
