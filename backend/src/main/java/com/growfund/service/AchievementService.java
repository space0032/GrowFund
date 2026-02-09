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
            checkStrategist(user, farm);
            checkEfficientFarmer(user, farm);
            checkMasterFarmer(user, farm);
        }
    }

    private void checkFirstSteps(User user, Farm farm) {
        unlock(user, "FIRST_STEPS", "First Steps", "Plant your first crop.", "🌱");
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

    private void checkStrategist(User user, Farm farm) {
        // Unlock if user has planted 3 or more different crop types
        long distinctCrops = farm.getCrops().stream()
                .map(com.growfund.model.Crop::getCropType)
                .distinct()
                .count();

        if (distinctCrops >= 3) {
            unlock(user, "STRATEGIST", "Master Strategist", "Diversify your farm with 3 different crops.", "🧠");
        }
    }

    private void checkEfficientFarmer(User user, Farm farm) {
        // Unlock if any crop yielded more than 2.0x expected yield (lucky + good
        // management)
        // Check historical crops? Or just check if they HAVE an achievement?
        // Let's check current/past crops attached to farm
        boolean hasHighYield = farm.getCrops().stream()
                .anyMatch(c -> c.getActualYield() != null && c.getExpectedYield() != null &&
                        c.getActualYield() > c.getExpectedYield() * 1.5); // 1.5x is pretty good

        if (hasHighYield) {
            unlock(user, "EFFICIENT", "Efficient Farmer", "Achieve a bumper harvest (1.5x yield).", "📈");
        }
    }

    private void checkMasterFarmer(User user, Farm farm) {
        // "Master Farmer": Reward for achieving a profit margin of over 100%
        // Logic: (netProfit / totalCosts) * 100 > 100
        // We need to check if ANY crop provided this margin.

        boolean hasMasterProfit = farm.getCrops().stream()
                .anyMatch(c -> {
                    if ("HARVESTED".equals(c.getStatus()) && c.getProfit() != null && c.getInvestmentAmount() != null
                            && c.getInvestmentAmount() > 0) {
                        double margin = ((double) c.getProfit() / c.getInvestmentAmount()) * 100.0;
                        return margin > 100.0;
                    }
                    return false;
                });

        if (hasMasterProfit) {
            unlock(user, "MASTER_FARMER", "Master Farmer", "Achieve a profit margin of over 100%.", "🏆");
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
