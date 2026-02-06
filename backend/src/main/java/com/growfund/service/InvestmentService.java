package com.growfund.service;

import com.growfund.model.Investment;
import com.growfund.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing investment calculations and operations
 * Uses Apache Commons Math for financial calculations
 */
@Service
@RequiredArgsConstructor
public class InvestmentService {
    
    private final InvestmentRepository investmentRepository;
    
    /**
     * Calculate compound interest for an investment
     * Formula: A = P(1 + r/n)^(nt)
     * where A = final amount, P = principal, r = annual rate, n = compounds per year, t = time in years
     */
    public double calculateCompoundInterest(double principal, double annualRate, int months, int compoundsPerYear) {
        double rate = annualRate / 100.0;
        double time = months / 12.0;
        double amount = principal * Math.pow(1 + (rate / compoundsPerYear), compoundsPerYear * time);
        return Precision.round(amount, 2);
    }
    
    /**
     * Calculate simple interest for an investment
     * Formula: A = P(1 + rt)
     */
    public double calculateSimpleInterest(double principal, double annualRate, int months) {
        double rate = annualRate / 100.0;
        double time = months / 12.0;
        double amount = principal * (1 + rate * time);
        return Precision.round(amount, 2);
    }
    
    @Transactional
    public Investment createInvestment(Investment investment) {
        investment.setStartDate(LocalDateTime.now());
        investment.setMaturityDate(investment.getStartDate().plusMonths(investment.getDurationMonths()));
        investment.setStatus("ACTIVE");
        
        // Calculate expected maturity value based on investment type
        double maturityValue = calculateCompoundInterest(
            investment.getPrincipalAmount(),
            investment.getInterestRate(),
            investment.getDurationMonths(),
            12 // Monthly compounding
        );
        investment.setCurrentValue((long) maturityValue);
        
        return investmentRepository.save(investment);
    }
    
    public List<Investment> getUserInvestments(Long userId) {
        return investmentRepository.findByUserId(userId);
    }
    
    public List<Investment> getActiveInvestments(Long userId) {
        return investmentRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }
    
    @Transactional
    public Investment updateInvestmentValue(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
            .orElseThrow(() -> new RuntimeException("Investment not found"));
        
        // Calculate current value based on time elapsed
        long monthsElapsed = java.time.temporal.ChronoUnit.MONTHS.between(
            investment.getStartDate(), LocalDateTime.now()
        );
        
        double currentValue = calculateCompoundInterest(
            investment.getPrincipalAmount(),
            investment.getInterestRate(),
            (int) monthsElapsed,
            12
        );
        
        investment.setCurrentValue((long) currentValue);
        
        // Check if matured
        if (LocalDateTime.now().isAfter(investment.getMaturityDate())) {
            investment.setStatus("MATURED");
            investment.setCompletedAt(LocalDateTime.now());
        }
        
        return investmentRepository.save(investment);
    }
}
