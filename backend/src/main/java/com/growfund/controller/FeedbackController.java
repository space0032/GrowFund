package com.growfund.controller;

import com.growfund.model.Feedback;
import com.growfund.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    @PostMapping
    public ResponseEntity<?> submitFeedback(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal String uid) {

        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Feedback content is required");
        }

        Feedback feedback = new Feedback();
        feedback.setUserId(uid);
        feedback.setContent(content);
        feedback.setAppVersion(request.get("appVersion"));
        feedback.setDeviceModel(request.get("deviceModel"));

        feedbackRepository.save(feedback);

        return ResponseEntity.ok("Feedback received");
    }

    @GetMapping
    public ResponseEntity<java.util.List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackRepository.findAll());
    }
}
