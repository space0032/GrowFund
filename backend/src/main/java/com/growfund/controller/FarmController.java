package com.growfund.controller;

import com.growfund.dto.FarmDTO;
import com.growfund.model.User;
import com.growfund.service.FarmService;
import com.growfund.service.UserService;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<FarmDTO> createFarm(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String uid) {

        String farmName = request.get("farmName");
        if (farmName == null || farmName.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.getUserByFirebaseUid(uid);
        FarmDTO farm = farmService.createFarm(user.getId(), farmName);
        return ResponseEntity.ok(farm);
    }

    @GetMapping("/my-farm")
    public ResponseEntity<FarmDTO> getMyFarm(@AuthenticationPrincipal String uid) {
        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }

        try {
            // Get user by Firebase UID
            User user = userService.getUserByFirebaseUid(uid);
            FarmDTO farm = farmService.getFarmByUserId(user.getId());
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
