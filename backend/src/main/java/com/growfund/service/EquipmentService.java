package com.growfund.service;

import com.growfund.model.Equipment;
import com.growfund.model.Farm;
import com.growfund.model.FarmEquipment;
import com.growfund.repository.EquipmentRepository;
import com.growfund.repository.FarmEquipmentRepository;
import com.growfund.repository.FarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final FarmEquipmentRepository farmEquipmentRepository;
    private final FarmRepository farmRepository;

    /**
     * Get all available equipment in the catalog
     */
    public List<Equipment> getAllEquipment() {
        return equipmentRepository.findAll();
    }

    /**
     * Get equipment by type
     */
    public List<Equipment> getEquipmentByType(String equipmentType) {
        return equipmentRepository.findByEquipmentType(equipmentType);
    }

    /**
     * Get equipment within budget
     */
    public List<Equipment> getAffordableEquipment(Long maxBudget) {
        return equipmentRepository.findByCostLessThanEqual(maxBudget);
    }

    /**
     * Get all equipment owned by a farm
     */
    public List<FarmEquipment> getFarmEquipment(Long farmId) {
        return farmEquipmentRepository.findByFarmId(farmId);
    }

    /**
     * Get active/usable equipment for a farm
     */
    public List<FarmEquipment> getUsableEquipment(Long farmId) {
        return farmEquipmentRepository.findUsableEquipmentByFarmId(farmId);
    }

    /**
     * Purchase equipment for a farm
     */
    @Transactional
    public FarmEquipment purchaseEquipment(Long farmId, Long equipmentId) {
        // Get farm
        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new RuntimeException("Farm not found"));

        // Get equipment
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        // Check if farm has enough savings
        if (farm.getSavings() < equipment.getCost()) {
            throw new RuntimeException("Insufficient funds. Need ₹" + equipment.getCost() +
                    " but have ₹" + farm.getSavings());
        }

        // Deduct cost from farm savings
        farm.setSavings(farm.getSavings() - equipment.getCost());
        farmRepository.save(farm);

        // Create farm equipment record
        FarmEquipment farmEquipment = new FarmEquipment();
        farmEquipment.setFarm(farm);
        farmEquipment.setEquipment(equipment);
        farmEquipment.setDurabilityRemaining(equipment.getMaxDurability());
        farmEquipment.setActive(true);

        return farmEquipmentRepository.save(farmEquipment);
    }

    /**
     * Calculate total bonuses from all active equipment for a farm
     */
    public Map<String, Double> calculateTotalBonuses(Long farmId) {
        List<FarmEquipment> usableEquipment = getUsableEquipment(farmId);

        double totalYieldBonus = 0.0;
        double totalCostReduction = 0.0;

        for (FarmEquipment farmEquip : usableEquipment) {
            Equipment equip = farmEquip.getEquipment();
            if (equip.getYieldBonus() != null) {
                totalYieldBonus += equip.getYieldBonus();
            }
            if (equip.getCostReduction() != null) {
                totalCostReduction += equip.getCostReduction();
            }
        }

        Map<String, Double> bonuses = new HashMap<>();
        bonuses.put("yieldBonus", totalYieldBonus);
        bonuses.put("costReduction", totalCostReduction);
        bonuses.put("yieldMultiplier", 1.0 + totalYieldBonus);
        bonuses.put("costMultiplier", 1.0 - totalCostReduction);

        return bonuses;
    }

    /**
     * Use equipment (reduce durability)
     */
    @Transactional
    public void useEquipment(Long farmId) {
        List<FarmEquipment> usableEquipment = getUsableEquipment(farmId);

        for (FarmEquipment farmEquip : usableEquipment) {
            farmEquip.use();
            farmEquipmentRepository.save(farmEquip);
        }
    }

    /**
     * Get equipment statistics for a farm
     */
    public Map<String, Object> getEquipmentStats(Long farmId) {
        List<FarmEquipment> allEquipment = getFarmEquipment(farmId);
        List<FarmEquipment> usableEquipment = getUsableEquipment(farmId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEquipment", allEquipment.size());
        stats.put("activeEquipment", usableEquipment.size());
        stats.put("bonuses", calculateTotalBonuses(farmId));

        return stats;
    }

    /**
     * Check if farm owns specific equipment
     */
    public boolean ownsEquipment(Long farmId, Long equipmentId) {
        return farmEquipmentRepository.existsByFarmIdAndEquipmentId(farmId, equipmentId);
    }
}
