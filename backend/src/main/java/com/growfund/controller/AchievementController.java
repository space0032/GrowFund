package com.growfund.controller;

import com.growfund.model.Achievement;
import com.growfund.model.User;
import com.growfund.service.AchievementService;
import com.growfund.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<Achievement>> getMyAchievements(@AuthenticationPrincipal String uid) {
        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByFirebaseUid(uid);
        return ResponseEntity.ok(achievementService.getUserAchievements(user.getId()));
    }
}
