package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Equipment entity for farm equipment and tools
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
    
    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;
    
    @Column(nullable = false)
    private String equipmentType; // TRACTOR, PUMP, SPRAYER, etc.
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Long purchasePrice;
    
    private Long currentValue;
    
    @Column(nullable = false)
    private Integer durability = 100; // Percentage
    
    @Column(nullable = false)
    private Boolean isUnlocked = false;
}
