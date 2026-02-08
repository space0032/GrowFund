package com.growfund.service;

import com.growfund.dto.AnalyticsDTO;
import com.growfund.model.*;
import com.growfund.repository.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
        private final FarmRepository farmRepository;
        private final InvestmentRepository investmentRepository;
        private final CropRepository cropRepository;
        private final FarmEquipmentRepository farmEquipmentRepository;

        public AnalyticsDTO getFarmAnalytics(Long farmId) {
                try {
                        Farm farm = farmRepository.findById(farmId)
                                        .orElseThrow(() -> new RuntimeException("Farm not found"));

                        // 1. Savings
                        Long savings = farm.getSavings() != null ? farm.getSavings() : 0L;

                        // 2. Investment Value & Distribution
                        List<Investment> investments = investmentRepository.findByUserId(farm.getUser().getId());

                        Long totalInvestmentValue = 0L;
                        Map<String, Long> distribution = new HashMap<>();

                        for (Investment inv : investments) {
                                Long principal = inv.getPrincipalAmount() != null ? inv.getPrincipalAmount() : 0L;
                                Long val = inv.getCurrentValue() != null ? inv.getCurrentValue() : principal;

                                if ("ACTIVE".equals(inv.getStatus())) {
                                        totalInvestmentValue += val;
                                        distribution.merge(inv.getInvestmentType(), val, Long::sum);
                                }
                        }

                        // 3. Crop Value (Active Crops)
                        List<Crop> activeCrops = cropRepository.findByFarmId(farmId).stream()
                                        .filter(c -> "PLANTED".equals(c.getStatus()) || "GROWING".equals(c.getStatus())
                                                        || "READY".equals(c.getStatus()))
                                        .collect(Collectors.toList());

                        Long totalCropValue = activeCrops.stream()
                                        .mapToLong(c -> c.getInvestmentAmount() != null ? c.getInvestmentAmount() : 0L)
                                        .sum();

                        // 4. Equipment Value
                        List<FarmEquipment> equipment = farmEquipmentRepository.findByFarmId(farmId);
                        Long totalEquipmentValue = equipment.stream()
                                        .mapToLong(e -> (e.getEquipment() != null && e.getEquipment().getCost() != null)
                                                        ? e.getEquipment().getCost()
                                                        : 0L)
                                        .sum();

                        // 5. ROI Calculations
                        List<Crop> harvestedCrops = cropRepository.findByFarmId(farmId).stream()
                                        .filter(c -> "HARVESTED".equals(c.getStatus()) || "SOLD".equals(c.getStatus()))
                                        .collect(Collectors.toList());

                        long totalHistoricalCost = 0;
                        long totalHistoricalRevenue = 0;

                        for (Crop c : harvestedCrops) {
                                totalHistoricalCost += c.getInvestmentAmount() != null ? c.getInvestmentAmount() : 0L;
                                if (c.getRevenue() != null)
                                        totalHistoricalRevenue += c.getRevenue();
                        }

                        double historicalRoi = 0.0;
                        if (totalHistoricalCost > 0) {
                                historicalRoi = ((double) (totalHistoricalRevenue - totalHistoricalCost)
                                                / totalHistoricalCost) * 100;
                        }

                        double projectedRoi = 15.0; // Placeholder

                        return AnalyticsDTO.builder()
                                        .totalPortfolioValue(savings + totalInvestmentValue + totalCropValue
                                                        + totalEquipmentValue)
                                        .savings(savings)
                                        .investmentValue(totalInvestmentValue)
                                        .cropValue(totalCropValue)
                                        .equipmentValue(totalEquipmentValue)
                                        .investmentDistribution(distribution)
                                        .historicalRoi(historicalRoi)
                                        .projectedRoi(projectedRoi)
                                        .build();
                } catch (Exception e) {
                        e.printStackTrace(); // Log the error
                        throw e; // Re-throw to let controller handle it
                }
        }
}
