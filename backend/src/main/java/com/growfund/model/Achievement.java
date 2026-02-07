package com.growfund.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

/**
 * Achievement entity for gamification
 */
@Entity
@Table(name = "achievements")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(nullable = false)
    private String achievementType; // Used as ID

    @Column(nullable = false)
    private String title;

    private String description;

    private String icon; // Added icon

    @Column(nullable = false)
    private Long rewardCoins = 0L;

    @Column(nullable = false)
    private Long rewardExperience = 0L;

    @Column(nullable = false)
    private LocalDateTime unlockedAt = LocalDateTime.now();

    @JsonIgnore
    public String getAchievementId() {
        return achievementType;
    }

    public void setAchievementId(String achievementId) {
        this.achievementType = achievementId;
    }
}
