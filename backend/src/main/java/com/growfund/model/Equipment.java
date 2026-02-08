package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Equipment catalog entity - represents available equipment types
 */
@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "equipment_type", nullable = false)
    private String equipmentType; // IRRIGATION, FERTILIZER, TOOLS, SEEDS

    @Column(nullable = false)
    private String tier; // BASIC, ADVANCED, PREMIUM

    @Column(nullable = false)
    private Long cost; // Purchase price in rupees

    @Column(name = "yield_bonus")
    private Double yieldBonus; // Percentage bonus to crop yield (e.g., 0.15 = 15%)

    @Column(name = "cost_reduction")
    private Double costReduction; // Percentage reduction in planting cost (e.g., 0.10 = 10%)

    @Column(name = "max_durability", nullable = false)
    private Integer maxDurability; // Maximum number of uses

    private String icon; // Emoji or icon identifier

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Equipment Types
    public static final String IRRIGATION = "IRRIGATION";
    public static final String FERTILIZER = "FERTILIZER";
    public static final String TOOLS = "TOOLS";
    public static final String SEEDS = "SEEDS";

    // Tiers
    public static final String BASIC = "BASIC";
    public static final String ADVANCED = "ADVANCED";
    public static final String PREMIUM = "PREMIUM";

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
}
