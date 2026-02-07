package com.growfund.seedtowealth.model;

import java.io.Serializable;

public class Investment implements Serializable {
    private Long id;
    private Long userId; // Optional, handled efficiently by backend
    private String name;
    private String type; // "FD", "MUTUAL_FUND", "STOCK"
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

    public Investment(String name, String type, Double principalAmount, Double interestRate, Integer durationMonths) {
        this.name = name;
        this.type = type;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
