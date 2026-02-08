package com.growfund.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank(message = "Investment type is required")
    private String investmentType; // MUTUAL_FUND, FIXED_DEPOSIT, KISAN_VIKAS_PATRA, etc.

    @Column(nullable = false)
    @NotBlank(message = "Scheme name is required")
    private String schemeName;

    @Column(nullable = false)
    @NotNull(message = "Principal amount is required")
    @Min(value = 1, message = "Principal amount must be positive")
    private Long principalAmount;

    @Column(nullable = false)
    @NotNull(message = "Interest rate is required")
    @Min(value = 0, message = "Interest rate must be non-negative")
    private Double interestRate;

    @Column(nullable = false)
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    private Integer durationMonths;

    private Long currentValue;

    @Column(nullable = false)
    private LocalDateTime startDate = LocalDateTime.now();

    private LocalDateTime maturityDate;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, MATURED, WITHDRAWN

    private LocalDateTime completedAt;
}
