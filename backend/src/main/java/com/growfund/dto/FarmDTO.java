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
    private Long savings;
    private Long emergencyFund;
    private Integer cropCount;
}
