package com.growfund.service;

import com.growfund.model.Achievement;
import com.growfund.model.User;
import com.growfund.model.Farm;
import com.growfund.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementService {

    private final AchievementRepository achievementRepository;

    public void checkAll(User user, Farm farm) {
        checkFirstSteps(user, farm);
        checkTycoon(user, farm);
        if (farm != null) {
            checkLandBaron(user, farm);
        }
    }

    private void checkFirstSteps(User user, Farm farm) {
        unlock(user, "FIRST_STEPS", "First Steps", "Plant your first crop.", "🌱");
        // Logic: Checks if user has planted anything. For now just unlock on first
        // login or activity?
        // Better: Called from plantCrop/harvestCrop
    }

    private void checkTycoon(User user, Farm farm) {
        if (farm != null && farm.getSavings() >= 100000) {
            unlock(user, "TYCOON", "Tycoon", "Reach ₹100,000 in savings.", "💰");
        }
    }

    private void checkLandBaron(User user, Farm farm) {
        if (farm.getLandSize() >= 5.0) {
            unlock(user, "LAND_BARON", "Land Baron", "Reach 5 acres of land.", "🚜");
        }
    }

    @Transactional
    public void unlock(User user, String achievementId, String title, String description, String icon) {
        Optional<Achievement> existing = achievementRepository.findByUserIdAndAchievementType(user.getId(),
                achievementId);
        if (existing.isEmpty()) {
            Achievement achievement = new Achievement();
            achievement.setUser(user);
            achievement.setAchievementType(achievementId);
            achievement.setTitle(title);
            achievement.setDescription(description);
            achievement.setIcon(icon);
            achievement.setRewardCoins(100L);
            achievement.setRewardExperience(50L);
            achievement.setUnlockedAt(LocalDateTime.now());
            achievementRepository.save(achievement);
        }
    }

    public List<Achievement> getUserAchievements(Long userId) {
        return achievementRepository.findByUserId(userId);
    }
}
