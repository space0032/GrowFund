package com.growfund.controller;

import com.growfund.model.Investment;
import com.growfund.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for investment-related operations
 */
@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
public class InvestmentController {
    
    private final InvestmentService investmentService;
    
    @PostMapping
    public ResponseEntity<Investment> createInvestment(@RequestBody Investment investment) {
        Investment created = investmentService.createInvestment(investment);
        return ResponseEntity.ok(created);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Investment>> getUserInvestments(@PathVariable Long userId) {
        List<Investment> investments = investmentService.getUserInvestments(userId);
        return ResponseEntity.ok(investments);
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<Investment>> getActiveInvestments(@PathVariable Long userId) {
        List<Investment> investments = investmentService.getActiveInvestments(userId);
        return ResponseEntity.ok(investments);
    }
    
    @PutMapping("/{investmentId}/update-value")
    public ResponseEntity<Investment> updateInvestmentValue(@PathVariable Long investmentId) {
        Investment updated = investmentService.updateInvestmentValue(investmentId);
        return ResponseEntity.ok(updated);
    }
    
    @GetMapping("/calculate/compound")
    public ResponseEntity<Double> calculateCompoundInterest(
            @RequestParam double principal,
            @RequestParam double annualRate,
            @RequestParam int months,
            @RequestParam(defaultValue = "12") int compoundsPerYear) {
        double result = investmentService.calculateCompoundInterest(principal, annualRate, months, compoundsPerYear);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/calculate/simple")
    public ResponseEntity<Double> calculateSimpleInterest(
            @RequestParam double principal,
            @RequestParam double annualRate,
            @RequestParam int months) {
        double result = investmentService.calculateSimpleInterest(principal, annualRate, months);
        return ResponseEntity.ok(result);
    }
}
