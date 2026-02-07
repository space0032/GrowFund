package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

/**
 * Crop entity representing crops planted on a farm
 */
@Entity
@Table(name = "crops")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farm_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Farm farm;

    @Column(nullable = false)
    private String cropType; // WHEAT, RICE, COTTON, etc.

    @Column(nullable = false)
    private Double areaPlanted; // in acres

    @Column(nullable = false)
    private Long investmentAmount;

    private Long expectedYield;

    private Long actualYield;

    @Column(nullable = false)
    private String season; // KHARIF, RABI, ZAID

    @Column(nullable = false)
    private LocalDateTime plantedDate = LocalDateTime.now();

    private LocalDateTime harvestDate;

    @Column(nullable = false)
    private String status = "PLANTED"; // PLANTED, GROWING, HARVESTED, FAILED

    private String weatherImpact; // NORMAL, DROUGHT, FLOOD, PEST

    private Long revenue;

    private Long profit;

    private Long sellingPricePerUnit;
}
