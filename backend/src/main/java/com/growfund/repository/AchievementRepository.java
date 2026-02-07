package com.growfund.repository;

import com.growfund.model.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    List<Achievement> findByUserId(Long userId);

    Optional<Achievement> findByUserIdAndAchievementType(Long userId, String achievementType);
}
