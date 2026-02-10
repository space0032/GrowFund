package com.growfund.controller;

import com.growfund.dto.UserDTO;
import com.growfund.dto.UserResponseDTO;
import com.growfund.model.User;
import com.growfund.service.UserService;
import com.growfund.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FarmService farmService;

    @PostMapping("/sync")
    public ResponseEntity<UserResponseDTO> syncUser(@RequestBody UserDTO userDTO,
            @AuthenticationPrincipal String uid) {
        try {
            // We could verify the UID here if needed, but the Filter already verified the
            // token.
            // Ideally we trust the token's claims.
            userDTO.setUid(uid);
            User user = userService.syncUser(userDTO);
            return ResponseEntity.ok(convertToResponseDTO(user));
        } catch (Exception e) {
            e.printStackTrace(); // Log to console
            return ResponseEntity.internalServerError().build();
        }
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirebaseUid(user.getFirebaseUid());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setPreferredLanguage(user.getPreferredLanguage());
        dto.setState(user.getState());
        dto.setDistrict(user.getDistrict());
        dto.setCurrentLevel(user.getCurrentLevel());
        dto.setTotalCoins(user.getTotalCoins());
        dto.setExperience(user.getExperience());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginAt(user.getLastLoginAt());

        if (user.getFarm() != null) {
            try {
                // Use service to get DTO with calculated fields (expansionCost)
                dto.setFarm(farmService.getFarmByUserId(user.getId()));
            } catch (Exception e) {
                // Fallback or log if farm service fails
                e.printStackTrace();
            }
        }

        dto.setInvestments(user.getInvestments());
        dto.setAchievements(user.getAchievements());

        return dto;
    }
}
