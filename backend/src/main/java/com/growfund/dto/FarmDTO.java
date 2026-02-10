package com.growfund.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmDTO {
    private Long id;
    private String farmName;
    private Double landSize;
    private Double availableLand; // Land not currently planted
    private Long savings;
    private Long emergencyFund;
    private Integer cropCount;
    private Long expansionCost; // Cost for next expansion
}
