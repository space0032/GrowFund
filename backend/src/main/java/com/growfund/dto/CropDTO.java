package com.growfund.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropDTO {
    private Long id;
    private String cropType;
    private Double areaPlanted;
    private Long investmentAmount;
    private Long expectedYield;
    private Long actualYield;
    private String season;
    private LocalDateTime plantedDate;
    private LocalDateTime harvestDate;
    private String status;
    private String weatherImpact;
}
