package com.growfund.controller;

import com.growfund.model.Equipment;
import com.growfund.model.FarmEquipment;
import com.growfund.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Get all available equipment
     */
    @GetMapping
    public ResponseEntity<List<Equipment>> getAllEquipment() {
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    /**
     * Get equipment by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Equipment>> getEquipmentByType(@PathVariable String type) {
        return ResponseEntity.ok(equipmentService.getEquipmentByType(type));
    }

    /**
     * Get affordable equipment within budget
     */
    @GetMapping("/affordable/{maxBudget}")
    public ResponseEntity<List<Equipment>> getAffordableEquipment(@PathVariable Long maxBudget) {
        return ResponseEntity.ok(equipmentService.getAffordableEquipment(maxBudget));
    }

    /**
     * Get farm's equipment
     */
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<FarmEquipment>> getFarmEquipment(@PathVariable Long farmId) {
        return ResponseEntity.ok(equipmentService.getFarmEquipment(farmId));
    }

    /**
     * Get farm's usable equipment
     */
    @GetMapping("/farm/{farmId}/usable")
    public ResponseEntity<List<FarmEquipment>> getUsableEquipment(@PathVariable Long farmId) {
        return ResponseEntity.ok(equipmentService.getUsableEquipment(farmId));
    }

    /**
     * Purchase equipment
     */
    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchaseEquipment(
            @RequestParam Long farmId,
            @RequestParam Long equipmentId) {
        try {
            FarmEquipment farmEquipment = equipmentService.purchaseEquipment(farmId, equipmentId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Equipment purchased successfully!");
            response.put("farmEquipment", farmEquipment);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get total bonuses for a farm
     */
    @GetMapping("/bonuses/{farmId}")
    public ResponseEntity<Map<String, Double>> getTotalBonuses(@PathVariable Long farmId) {
        return ResponseEntity.ok(equipmentService.calculateTotalBonuses(farmId));
    }

    /**
     * Get equipment statistics
     */
    @GetMapping("/stats/{farmId}")
    public ResponseEntity<Map<String, Object>> getEquipmentStats(@PathVariable Long farmId) {
        return ResponseEntity.ok(equipmentService.getEquipmentStats(farmId));
    }

    /**
     * Check if farm owns equipment
     */
    @GetMapping("/owns/{farmId}/{equipmentId}")
    public ResponseEntity<Boolean> ownsEquipment(
            @PathVariable Long farmId,
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.ownsEquipment(farmId, equipmentId));
    }
}
