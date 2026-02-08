package com.growfund.controller;

import com.growfund.model.RandomEvent;
import com.growfund.service.RandomEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RandomEventController {

    private final RandomEventService eventService;

    /**
     * Get all currently active events.
     */
    @GetMapping("/active")
    public ResponseEntity<List<RandomEvent>> getActiveEvents() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    /**
     * Get event history (last 30 days).
     */
    @GetMapping("/history")
    public ResponseEntity<List<RandomEvent>> getEventHistory() {
        return ResponseEntity.ok(eventService.getEventHistory());
    }

    /**
     * Get active events for a specific crop type.
     */
    @GetMapping("/active/crop/{cropType}")
    public ResponseEntity<List<RandomEvent>> getActiveEventsForCrop(@PathVariable String cropType) {
        return ResponseEntity.ok(eventService.getActiveEventsForCrop(cropType));
    }

    /**
     * Manually trigger event generation (can be called periodically or on certain
     * actions).
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateEvent() {
        Optional<RandomEvent> event = eventService.generateRandomEvent();

        if (event.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "generated", true,
                    "event", event.get()));
        } else {
            return ResponseEntity.ok(Map.of(
                    "generated", false,
                    "message", "No event generated this time"));
        }
    }

    /**
     * Manually trigger a specific event (for testing/admin purposes).
     */
    @PostMapping("/trigger")
    public ResponseEntity<RandomEvent> triggerEvent(
            @RequestParam String eventType,
            @RequestParam(defaultValue = "MEDIUM") String severity) {
        RandomEvent event = eventService.triggerEvent(eventType, severity);
        return ResponseEntity.ok(event);
    }

    /**
     * End an event manually.
     */
    @PostMapping("/end/{eventId}")
    public ResponseEntity<Map<String, String>> endEvent(@PathVariable Long eventId) {
        eventService.endEvent(eventId);
        return ResponseEntity.ok(Map.of("message", "Event ended successfully"));
    }
}
