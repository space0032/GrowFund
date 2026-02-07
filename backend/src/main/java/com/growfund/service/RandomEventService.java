package com.growfund.service;

import com.growfund.model.RandomEvent;
import com.growfund.repository.RandomEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RandomEventService {

    private final RandomEventRepository eventRepository;
    private final Random random = new Random();

    // Event generation probabilities (per check)
    private static final double EVENT_PROBABILITY = 0.15; // 15% chance
    private static final double LOW_SEVERITY_PROB = 0.60;
    private static final double MEDIUM_SEVERITY_PROB = 0.30;
    // HIGH is remaining 10%

    /**
     * Check if a new event should be generated and create it if needed.
     * This should be called periodically (e.g., daily or on certain actions).
     */
    public Optional<RandomEvent> generateRandomEvent() {
        // Check if there's already an active major event
        List<RandomEvent> activeEvents = getActiveEvents();
        if (hasMajorEventActive(activeEvents)) {
            return Optional.empty();
        }

        // Roll for event generation
        if (random.nextDouble() > EVENT_PROBABILITY) {
            return Optional.empty(); // No event this time
        }

        // Generate a new event
        RandomEvent event = createNewEvent();
        return Optional.of(eventRepository.save(event));
    }

    /**
     * Create a new random event with random type, severity, and duration.
     */
    private RandomEvent createNewEvent() {
        RandomEvent event = new RandomEvent();

        // Select random event type
        String[] eventTypes = {
                RandomEvent.DROUGHT,
                RandomEvent.PEST_ATTACK,
                RandomEvent.BONUS_RAIN,
                RandomEvent.MARKET_SURGE,
                RandomEvent.MARKET_CRASH,
                RandomEvent.HEATWAVE,
                RandomEvent.GOVERNMENT_SUBSIDY
        };
        String eventType = eventTypes[random.nextInt(eventTypes.length)];
        event.setEventType(eventType);

        // Determine severity
        double severityRoll = random.nextDouble();
        String severity;
        if (severityRoll < LOW_SEVERITY_PROB) {
            severity = RandomEvent.LOW;
        } else if (severityRoll < LOW_SEVERITY_PROB + MEDIUM_SEVERITY_PROB) {
            severity = RandomEvent.MEDIUM;
        } else {
            severity = RandomEvent.HIGH;
        }
        event.setSeverity(severity);

        // Set time parameters
        LocalDateTime now = LocalDateTime.now();
        event.setStartTime(now);

        // Duration: 1-3 days based on severity
        int durationDays = switch (severity) {
            case RandomEvent.LOW -> 1;
            case RandomEvent.MEDIUM -> 2;
            case RandomEvent.HIGH -> 3;
            default -> 1;
        };
        event.setEndTime(now.plusDays(durationDays));

        // Set impact multiplier based on event type and severity
        event.setImpactMultiplier(calculateImpactMultiplier(eventType, severity));

        // Set description
        event.setDescription(generateEventDescription(eventType, severity));

        // For some events, set affected crop type
        if (eventType.equals(RandomEvent.PEST_ATTACK)) {
            String[] cropTypes = { "WHEAT", "RICE", "CORN", "COTTON", "SUGARCANE" };
            event.setAffectedCropType(cropTypes[random.nextInt(cropTypes.length)]);
        }

        event.setActive(true);
        event.setCreatedAt(now);

        return event;
    }

    /**
     * Calculate impact multiplier based on event type and severity.
     */
    private Double calculateImpactMultiplier(String eventType, String severity) {
        double baseImpact = switch (eventType) {
            case RandomEvent.DROUGHT -> 0.70; // -30% yield
            case RandomEvent.PEST_ATTACK -> 0.75; // -25% yield
            case RandomEvent.BONUS_RAIN -> 1.20; // +20% yield
            case RandomEvent.MARKET_SURGE -> 1.40; // +40% price
            case RandomEvent.MARKET_CRASH -> 0.65; // -35% price
            case RandomEvent.HEATWAVE -> 0.80; // -20% yield
            case RandomEvent.GOVERNMENT_SUBSIDY -> 0.80; // -20% cost
            default -> 1.0;
        };

        // Adjust based on severity
        double severityMultiplier = switch (severity) {
            case RandomEvent.LOW -> 0.5; // Half the base impact
            case RandomEvent.MEDIUM -> 1.0; // Full base impact
            case RandomEvent.HIGH -> 1.5; // 1.5x the base impact
            default -> 1.0;
        };

        // Calculate final multiplier
        double impact = 1.0 + (baseImpact - 1.0) * severityMultiplier;
        return Math.round(impact * 100.0) / 100.0; // Round to 2 decimals
    }

    /**
     * Generate human-readable description for the event.
     */
    private String generateEventDescription(String eventType, String severity) {
        String severityText = severity.toLowerCase();

        return switch (eventType) {
            case RandomEvent.DROUGHT ->
                "A " + severityText + " drought has affected the region. Crops will grow slower and yield less.";
            case RandomEvent.PEST_ATTACK ->
                "A " + severityText + " pest infestation is attacking crops. Affected crops will have reduced yields.";
            case RandomEvent.BONUS_RAIN ->
                severityText.equals("high") ? "Excellent rainfall! Crops are thriving with increased growth and yield."
                        : "Good rainfall is boosting crop growth and yields.";
            case RandomEvent.MARKET_SURGE ->
                "Market demand has surged! Selling prices are significantly higher.";
            case RandomEvent.MARKET_CRASH ->
                "Market prices have dropped due to oversupply. Selling prices are lower than usual.";
            case RandomEvent.HEATWAVE ->
                "A " + severityText + " heatwave is affecting the region. Water-intensive crops are suffering.";
            case RandomEvent.GOVERNMENT_SUBSIDY ->
                "Government has announced farming subsidies! Planting costs are reduced.";
            default -> "An unexpected event has occurred.";
        };
    }

    /**
     * Get all currently active events.
     */
    public List<RandomEvent> getActiveEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<RandomEvent> events = eventRepository.findByActiveTrueAndEndTimeAfter(now);

        // Deactivate expired events
        events.forEach(event -> {
            if (event.getEndTime().isBefore(now)) {
                event.setActive(false);
                eventRepository.save(event);
            }
        });

        return events.stream().filter(RandomEvent::isActive).toList();
    }

    /**
     * Get event history (last 30 days).
     */
    public List<RandomEvent> getEventHistory() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return eventRepository.findByCreatedAtAfterOrderByCreatedAtDesc(thirtyDaysAgo);
    }

    /**
     * Get active events affecting a specific crop type.
     */
    public List<RandomEvent> getActiveEventsForCrop(String cropType) {
        List<RandomEvent> activeEvents = getActiveEvents();
        return activeEvents.stream()
                .filter(event -> event.getAffectedCropType() == null ||
                        event.getAffectedCropType().equals(cropType))
                .toList();
    }

    /**
     * Check if there's a major event currently active.
     * Major events: DROUGHT, MARKET_SURGE, MARKET_CRASH
     */
    private boolean hasMajorEventActive(List<RandomEvent> events) {
        return events.stream().anyMatch(event -> event.getEventType().equals(RandomEvent.DROUGHT) ||
                event.getEventType().equals(RandomEvent.MARKET_SURGE) ||
                event.getEventType().equals(RandomEvent.MARKET_CRASH));
    }

    /**
     * Manually trigger an event (for testing).
     */
    public RandomEvent triggerEvent(String eventType, String severity) {
        RandomEvent event = new RandomEvent();
        event.setEventType(eventType);
        event.setSeverity(severity);
        event.setStartTime(LocalDateTime.now());
        event.setEndTime(LocalDateTime.now().plusDays(2));
        event.setImpactMultiplier(calculateImpactMultiplier(eventType, severity));
        event.setDescription(generateEventDescription(eventType, severity));
        event.setActive(true);
        event.setCreatedAt(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * End an event manually.
     */
    public void endEvent(Long eventId) {
        eventRepository.findById(eventId).ifPresent(event -> {
            event.setActive(false);
            event.setEndTime(LocalDateTime.now());
            eventRepository.save(event);
        });
    }
}
