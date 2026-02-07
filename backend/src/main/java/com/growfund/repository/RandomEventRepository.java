package com.growfund.repository;

import com.growfund.model.RandomEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RandomEventRepository extends JpaRepository<RandomEvent, Long> {

    // Find all active events
    List<RandomEvent> findByActiveTrue();

    // Find active events within a time range
    List<RandomEvent> findByActiveTrueAndEndTimeAfter(LocalDateTime currentTime);

    // Find events by type
    List<RandomEvent> findByEventTypeAndActiveTrue(String eventType);

    // Find recent events (for history)
    List<RandomEvent> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime since);

    // Find events affecting specific crop
    List<RandomEvent> findByActiveTrueAndAffectedCropType(String cropType);
}
