package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Quiz entity representing a financial literacy question
 */
@Entity
@Table(name = "quizzes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;

    @Column(nullable = false)
    private Integer correctOptionIndex; // 0, 1, 2, or 3

    @Column(nullable = false)
    private Long rewardCoins = 50L;

    @Column(nullable = false)
    private Long rewardExperience = 20L;

    // Difficulty level? Category? Keeping simple for now.
}
