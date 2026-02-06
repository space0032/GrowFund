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
}
