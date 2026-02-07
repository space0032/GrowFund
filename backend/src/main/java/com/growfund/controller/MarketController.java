package com.growfund.controller;

import com.growfund.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/trends")
    public ResponseEntity<Map<String, Double>> getMarketTrends() {
        return ResponseEntity.ok(marketService.getMarketTrends());
    }
}
