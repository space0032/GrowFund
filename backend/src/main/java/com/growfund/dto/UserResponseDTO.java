package com.growfund.dto;

import com.growfund.model.Achievement;
import com.growfund.model.Investment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String firebaseUid;
    private String username;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String preferredLanguage;
    private String state;
    private String district;
    private Integer currentLevel;
    private Long totalCoins;
    private Long experience;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    private FarmDTO farm;
    private Set<Investment> investments;
    private Set<Achievement> achievements;
    // We can add other DTOs here if needed, or keep them as entities if they don't
    // need computation
    // For now, let's keep it simple and just focus on Farm which needs the DTO.
    // However, the original JSON response had investments and achievements.
    // If I return UserResponseDTO, I need to make sure I include those too.

    // Simplest approach: use Object or raw DTOs for others if they don't need
    // special logic
    // But better to be explicit.

    // Let's check what was in the original JSON:
    // "investments": [...], "achievements": [...]

    // I'll leave them as generic collections for now or create DTOs if I have time,
    // but to be safe and quick, I can map them from the entity.
    // Actually, since I'm manually converting, I should probably just return the
    // entity's collections
    // OR create DTOs for them too.

    // Let's verify existing DTOs first.
}
