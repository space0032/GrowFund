package com.growfund.service;

import com.growfund.dto.RecommendationDTO;
import com.growfund.model.Farm;
import com.growfund.repository.FarmRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final FarmRepository farmRepository;

    public List<RecommendationDTO> getRecommendations(Long farmId) {
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        List<RecommendationDTO> recommendations = new ArrayList<>();
        Long savings = farm.getSavings();

        // 1. Savings-based Recommendations
        if (savings < 10000) {
            // Low Savings -> Safe Recommendations
            recommendations.add(RecommendationDTO.builder()
                    .title("Plant Wheat during Rabi")
                    .description("Wheat is a reliable crop with low risk. Best planted in winter season.")
                    .type("SAFE")
                    .category("CROP")
                    .riskLevel("LOW")
                    .estimatedCost(5000L) // Per acre
                    .estimatedReturn(15.0) // 15% ROI
                    .iconName("ic_wheat")
                    .build());

            recommendations.add(RecommendationDTO.builder()
                    .title("Open Fixed Deposit")
                    .description("Secure your small savings with guaranteed returns.")
                    .type("SAFE")
                    .category("INVESTMENT")
                    .riskLevel("LOW")
                    .estimatedCost(1000L)
                    .estimatedReturn(6.5)
                    .iconName("ic_investment")
                    .build());
        } else if (savings < 50000) {
            // Medium Savings -> Balanced
            recommendations.add(RecommendationDTO.builder()
                    .title("Plant Rice")
                    .description("Rice offers good yields but requires more water.")
                    .type("GROWTH")
                    .category("CROP")
                    .riskLevel("MEDIUM")
                    .estimatedCost(8000L)
                    .estimatedReturn(25.0)
                    .iconName("ic_rice")
                    .build());

            recommendations.add(RecommendationDTO.builder()
                    .title("Invest in KVP")
                    .description("Kisan Vikas Patra doubles money in ~10 years. Good for medium term.")
                    .type("SAFE")
                    .category("INVESTMENT")
                    .riskLevel("LOW")
                    .estimatedCost(5000L)
                    .estimatedReturn(7.5)
                    .iconName("ic_investment")
                    .build());
        } else {
            // High Savings -> High Growth
            recommendations.add(RecommendationDTO.builder()
                    .title("Plant Cotton")
                    .description("Cash crop with high profit potential but sensitive to pests.")
                    .type("HIGH_YIELD")
                    .category("CROP")
                    .riskLevel("HIGH")
                    .estimatedCost(12000L)
                    .estimatedReturn(40.0)
                    .iconName("ic_cotton")
                    .build());

            recommendations.add(RecommendationDTO.builder()
                    .title("Mutual Funds - Equity")
                    .description("High risk, high reward investment associated with market performance.")
                    .type("HIGH_YIELD")
                    .category("INVESTMENT")
                    .riskLevel("HIGH")
                    .estimatedCost(10000L)
                    .estimatedReturn(12.0)
                    .iconName("ic_mutual_fund")
                    .build());
        }

        // 2. Diversification Tip (Always added)
        if (recommendations.size() > 0) {
            recommendations.add(RecommendationDTO.builder()
                    .title("Diversify Portfolio")
                    .description("Don't put all eggs in one basket. Mix crops and investments.")
                    .type("STRATEGY")
                    .category("TIP")
                    .riskLevel("LOW")
                    .estimatedCost(0L)
                    .estimatedReturn(0.0)
                    .iconName("ic_info")
                    .build());
        }

        return recommendations;
    }
}
