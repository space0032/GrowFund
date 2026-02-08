package com.growfund.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDTO {
    private Long totalPortfolioValue;
    private Long savings;
    private Long investmentValue;
    private Long cropValue;
    private Long equipmentValue;
    private Map<String, Long> investmentDistribution;
    private Double historicalRoi; // Percentage
    private Double projectedRoi; // Percentage
}
