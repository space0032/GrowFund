package com.growfund.controller;

import com.growfund.model.Investment;
import com.growfund.model.User;
import com.growfund.service.InvestmentService;
import com.growfund.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> createInvestment(
            @jakarta.validation.Valid @RequestBody Investment investment,
            @AuthenticationPrincipal String uid) {

        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByFirebaseUid(uid);
        investment.setUser(user);

        try {
            Investment created = investmentService.createInvestment(investment);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/my-investments")
    public ResponseEntity<List<Investment>> getMyInvestments(@AuthenticationPrincipal String uid) {
        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByFirebaseUid(uid);
        List<Investment> investments = investmentService.getUserInvestments(user.getId());
        return ResponseEntity.ok(investments);
    }

    @GetMapping("/user/my-active-investments")
    public ResponseEntity<List<Investment>> getMyActiveInvestments(@AuthenticationPrincipal String uid) {
        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        User user = userService.getUserByFirebaseUid(uid);
        List<Investment> investments = investmentService.getActiveInvestments(user.getId());
        return ResponseEntity.ok(investments);
    }

    @PutMapping("/{investmentId}/update-value")
    public ResponseEntity<Investment> updateInvestmentValue(
            @PathVariable Long investmentId,
            @AuthenticationPrincipal String uid) {

        if (uid == null || uid.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        // Ensure user owns the investment (logic inside service or check here)
        // keeping simple for now
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
