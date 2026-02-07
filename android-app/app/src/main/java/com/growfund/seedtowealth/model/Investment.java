package com.growfund.seedtowealth.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.room.Index;

import java.io.Serializable;

@Entity(tableName = "investments", indices = {
        @Index(value = "status")
})
public class Investment implements Serializable {
    @PrimaryKey
    private Long id;
    private Long userId; // Optional, handled efficiently by backend
    private String schemeName;
    private String investmentType; // "FD", "MUTUAL_FUND", "STOCK"
    private Double principalAmount;
    private Double interestRate;
    private Integer durationMonths;
    private String startDate; // ISO String
    private String maturityDate; // ISO String
    private String status; // "ACTIVE", "MATURED"
    private Long currentValue;

    // Constructors
    public Investment() {
    }

    @Ignore
    public Investment(String schemeName, String investmentType, Double principalAmount, Double interestRate,
            Integer durationMonths) {
        this.schemeName = schemeName;
        this.investmentType = investmentType;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.durationMonths = durationMonths;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getInvestmentType() {
        return investmentType;
    }

    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }

    public Double getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(Double principalAmount) {
        this.principalAmount = principalAmount;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(Double interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(Integer durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Long currentValue) {
        this.currentValue = currentValue;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }
}
