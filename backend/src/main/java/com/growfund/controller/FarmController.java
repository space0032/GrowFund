package com.growfund.controller;

import com.growfund.dto.FarmDTO;
import com.growfund.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    @PostMapping
    public ResponseEntity<FarmDTO> createFarm(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String uid) {

        String farmName = request.get("farmName");
        if (farmName == null || farmName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Get user ID from UserService using Firebase UID
        // For now, assuming userId is passed or we need to look it up
        Long userId = Long.parseLong(request.getOrDefault("userId", "1"));

        FarmDTO farm = farmService.createFarm(userId, farmName);
        return ResponseEntity.ok(farm);
    }

    @GetMapping("/my-farm")
    public ResponseEntity<FarmDTO> getMyFarm(@AuthenticationPrincipal String uid) {
        // TODO: Get userId from uid via UserService
        Long userId = 1L; // Placeholder

        try {
            FarmDTO farm = farmService.getFarmByUserId(userId);
            return ResponseEntity.ok(farm);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/savings")
    public ResponseEntity<FarmDTO> updateSavings(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {

        Long savings = request.get("savings");
        Long emergencyFund = request.get("emergencyFund");

        FarmDTO farm = farmService.updateFarmSavings(id, savings, emergencyFund);
        return ResponseEntity.ok(farm);
    }
}
