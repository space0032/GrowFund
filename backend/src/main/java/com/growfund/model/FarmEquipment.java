package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * FarmEquipment entity - tracks equipment owned by a farm
 */
@Entity
@Table(name = "farm_equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "purchase_date", nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();

    @Column(name = "durability_remaining", nullable = false)
    private Integer durabilityRemaining; // Number of uses remaining

    @Column(nullable = false)
    private Boolean active = true; // Whether equipment is still usable

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    // Helper methods
    public boolean isUsable() {
        return active && durabilityRemaining > 0;
    }

    public void use() {
        if (durabilityRemaining > 0) {
            durabilityRemaining--;
            lastUsed = LocalDateTime.now();
            if (durabilityRemaining == 0) {
                active = false;
            }
        }
    }

    public int getDurabilityPercentage() {
        if (equipment == null || equipment.getMaxDurability() == 0) {
            return 0;
        }
        return (int) ((durabilityRemaining * 100.0) / equipment.getMaxDurability());
    }
}
