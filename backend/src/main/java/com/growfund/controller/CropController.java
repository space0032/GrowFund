package com.growfund.controller;

import com.growfund.dto.CropDTO;
import com.growfund.model.User;
import com.growfund.service.CropService;
import com.growfund.service.FarmService;
import com.growfund.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class CropController {

    private final CropService cropService;
    private final FarmService farmService;
    private final UserService userService;

    @PostMapping("/farms/{farmId}/crops")
    public ResponseEntity<CropDTO> plantCrop(
            @PathVariable Long farmId,
            @jakarta.validation.Valid @RequestBody com.growfund.dto.PlantCropRequest request,
            @AuthenticationPrincipal String uid) {

        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        // Verify user owns the farm
        User user = userService.getUserByFirebaseUid(uid);
        if (!farmService.isFarmOwnedByUser(farmId, user.getId())) {
            return ResponseEntity.status(403).build();
        }

        CropDTO crop = cropService.plantCrop(
                farmId,
                request.getCropType(),
                request.getAreaPlanted(),
                request.getSeason());
        return ResponseEntity.ok(crop);
    }

    @GetMapping("/farms/{farmId}/crops/estimate-cost")
    public ResponseEntity<Long> getPlantingCostEstimate(
            @PathVariable Long farmId,
            @RequestParam String cropType,
            @RequestParam Double areaPlanted) {
        try {
            Long cost = cropService.calculatePlantingCost(farmId, cropType, areaPlanted);
            return ResponseEntity.ok(cost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/farms/{farmId}/crops")
    public ResponseEntity<List<CropDTO>> getCropsByFarm(@PathVariable Long farmId) {
        List<CropDTO> crops = cropService.getCropsByFarm(farmId);
        return ResponseEntity.ok(crops);
    }

    @GetMapping("/crops/{id}")
    public ResponseEntity<CropDTO> getCrop(@PathVariable Long id) {
        try {
            CropDTO crop = cropService.getCropById(id);
            return ResponseEntity.ok(crop);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/crops/{id}/harvest")
    public ResponseEntity<CropDTO> harvestCrop(@PathVariable Long id) {
        try {
            CropDTO crop = cropService.harvestCrop(id);
            return ResponseEntity.ok(crop);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/crops/{id}/status")
    public ResponseEntity<CropDTO> updateCropStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        String status = request.get("status");
        CropDTO crop = cropService.updateCropStatus(id, status);
        return ResponseEntity.ok(crop);
    }

    @GetMapping("/farms/{farmId}/crops/limits/{cropType}")
    public ResponseEntity<Map<String, Double>> getCropLimit(
            @PathVariable Long farmId,
            @PathVariable String cropType) {
        try {
            Map<String, Double> limit = cropService.getCropLimit(farmId, cropType);
            return ResponseEntity.ok(limit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/crops/{id}")
    public ResponseEntity<Void> deleteCrop(@PathVariable Long id) {
        try {
            cropService.deleteCrop(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
