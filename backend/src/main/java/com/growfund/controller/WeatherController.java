package com.growfund.controller;

import com.growfund.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentWeather() {
        WeatherService.WeatherCondition weather = weatherService.getCurrentWeather();

        Map<String, Object> response = new HashMap<>();
        response.put("condition", weather.name()); // ENUM name (SUNNY)
        response.put("displayName", weather.getDisplayName());
        response.put("growthMultiplier", weather.getGrowthMultiplier());

        return ResponseEntity.ok(response);
    }
}
