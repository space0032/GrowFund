package com.growfund.repository;

import com.growfund.model.FarmEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FarmEquipmentRepository extends JpaRepository<FarmEquipment, Long> {

    // Find all equipment owned by a farm
    List<FarmEquipment> findByFarmId(Long farmId);

    // Find active equipment for a farm
    List<FarmEquipment> findByFarmIdAndActiveTrue(Long farmId);

    // Find usable equipment (active and has durability)
    @Query("SELECT fe FROM FarmEquipment fe WHERE fe.farm.id = :farmId AND fe.active = true AND fe.durabilityRemaining > 0")
    List<FarmEquipment> findUsableEquipmentByFarmId(@Param("farmId") Long farmId);

    // Find equipment by type for a farm
    @Query("SELECT fe FROM FarmEquipment fe WHERE fe.farm.id = :farmId AND fe.equipment.equipmentType = :type AND fe.active = true")
    List<FarmEquipment> findByFarmIdAndEquipmentType(@Param("farmId") Long farmId, @Param("type") String equipmentType);

    // Check if farm owns specific equipment
    boolean existsByFarmIdAndEquipmentId(Long farmId, Long equipmentId);
}
