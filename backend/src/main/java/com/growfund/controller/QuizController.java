package com.growfund.controller;

import com.growfund.model.Quiz;
import com.growfund.model.User;
import com.growfund.service.QuizService;
import com.growfund.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;
    private final UserService userService;

    @GetMapping("/daily")
    public ResponseEntity<Quiz> getDailyQuiz(@AuthenticationPrincipal String uid) {
        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByFirebaseUid(uid);

        // Ensure sample quizzes exist
        quizService.createSampleQuizzes();

        return quizService.getDailyQuiz(user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<Boolean> submitAnswer(
            @PathVariable Long quizId,
            @RequestParam Integer optionIndex,
            @AuthenticationPrincipal String uid) {

        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByFirebaseUid(uid);

        boolean isCorrect = quizService.submitAnswer(user, quizId, optionIndex);
        return ResponseEntity.ok(isCorrect);
    }
}
