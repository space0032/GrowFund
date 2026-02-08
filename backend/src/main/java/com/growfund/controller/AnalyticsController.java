package com.growfund.controller;

import com.growfund.dto.AnalyticsDTO;
import com.growfund.dto.RecommendationDTO;
import com.growfund.service.AnalyticsService;
import com.growfund.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final RecommendationService recommendationService;

    @GetMapping("/dashboard/{farmId}")
    public ResponseEntity<AnalyticsDTO> getDashboard(@PathVariable Long farmId) {
        AnalyticsDTO analytics = analyticsService.getFarmAnalytics(farmId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/recommendations/{farmId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(@PathVariable Long farmId) {
        List<RecommendationDTO> recommendations = recommendationService.getRecommendations(farmId);
        return ResponseEntity.ok(recommendations);
    }
}
