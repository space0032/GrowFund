package com.growfund.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private String title;
    private String description;
    private String type; // SAFE, GROWTH, HIGH_YIELD
    private String category; // CROP, INVESTMENT, EQUIPMENT
    private String riskLevel; // LOW, MEDIUM, HIGH
    private Long estimatedCost;
    private Double estimatedReturn;
    private String iconName;
}
