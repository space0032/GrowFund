package com.growfund.repository;

import com.growfund.model.Farm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {
    Optional<Farm> findByUserId(Long userId);

    java.util.List<Farm> findTop10ByOrderBySavingsDesc();
}
