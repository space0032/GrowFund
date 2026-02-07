package com.growfund.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "random_events")
public class RandomEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventType; // DROUGHT, PEST_ATTACK, BONUS_RAIN, MARKET_SURGE, etc.

    @Column(nullable = false)
    private String severity; // LOW, MEDIUM, HIGH

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean active = true;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double impactMultiplier; // Effect on crops/prices (e.g., 0.8 = -20%, 1.3 = +30%)

    private String affectedCropType; // Null means all crops, otherwise specific type

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

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
}
