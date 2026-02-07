package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;

/**
 * Farm entity representing a farmer's virtual farm in the game
 */
@Entity
@Table(name = "farms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false)
    private String farmName;

    @Column(nullable = false)
    private Double landSize = 1.0; // in acres

    @Column(nullable = false)
    private Long savings = 0L;

    @Column(nullable = false)
    private Long emergencyFund = 0L;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Crop> crops = new HashSet<>();

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Equipment> equipment = new HashSet<>();
}
