package com.growfund.seedtowealth.model;

import java.io.Serializable;

public class Achievement implements Serializable {
    private Long id;
    private String achievementType;
    private String title;
    private String description;
    private String icon;
    private boolean unlocked; // Derived from unlockedAt is not null?
    // Actually backend returns unlocked achievements.
    // If we want to show LOCKED achievements, backend should return all with
    // status.
    // For now, let's assume backend returns "My Achievements" (unlocked ones).
    // Or maybe we want to show all possible?
    // The current backend implementation `getUserAchievements` returns
    // `findByUserId`.
    // It only returns UNLOCKED ones.
    // To show locked ones, we'd need a master list.
    // Let's stick to showing UNLOCKED achievements first.

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAchievementType() {
        return achievementType;
    }

    public void setAchievementType(String achievementType) {
        this.achievementType = achievementType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
