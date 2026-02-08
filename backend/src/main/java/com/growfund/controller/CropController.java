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
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal String uid) {

        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        // Verify user owns the farm
        User user = userService.getUserByFirebaseUid(uid);
        if (!farmService.isFarmOwnedByUser(farmId, user.getId())) {
            return ResponseEntity.status(403).build();
        }

        String cropType = (String) request.get("cropType");
        Double areaPlanted = Double.parseDouble(request.get("areaPlanted").toString());
        Long investmentAmount = Long.parseLong(request.get("investmentAmount").toString());
        String season = (String) request.getOrDefault("season", "KHARIF");

        CropDTO crop = cropService.plantCrop(farmId, cropType, areaPlanted, investmentAmount, season);
        return ResponseEntity.ok(crop);
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
