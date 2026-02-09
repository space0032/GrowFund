package com.growfund.repository;

import com.growfund.model.Crop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {
    List<Crop> findByFarmId(Long farmId);

    List<Crop> findByFarmIdAndStatus(Long farmId, String status);

    long countByFarmId(Long farmId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(c.areaPlanted), 0.0) FROM Crop c WHERE c.farm.id = :farmId AND c.status IN ('PLANTED', 'GROWING')")
    Double sumPlantedAreaByFarmId(@org.springframework.data.repository.query.Param("farmId") Long farmId);
}
