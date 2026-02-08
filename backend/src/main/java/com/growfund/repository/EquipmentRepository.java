package com.growfund.repository;

import com.growfund.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    // Find all equipment of a specific type
    List<Equipment> findByEquipmentType(String equipmentType);

    // Find equipment by tier
    List<Equipment> findByTier(String tier);

    // Find equipment by type and tier
    List<Equipment> findByEquipmentTypeAndTier(String equipmentType, String tier);

    // Find equipment within a price range
    List<Equipment> findByCostLessThanEqual(Long maxCost);
}
