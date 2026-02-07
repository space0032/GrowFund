package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Investment entity for tracking farmer's investment activities
 */
@Entity
@Table(name = "investments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false)
    private String investmentType; // MUTUAL_FUND, FIXED_DEPOSIT, KISAN_VIKAS_PATRA, etc.

    @Column(nullable = false)
    private String schemeName;

    @Column(nullable = false)
    private Long principalAmount;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Integer durationMonths;

    private Long currentValue;

    @Column(nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime maturityDate;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, MATURED, WITHDRAWN

    private LocalDateTime completedAt;
}
