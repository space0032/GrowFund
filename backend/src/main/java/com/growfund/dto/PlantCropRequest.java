package com.growfund.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PlantCropRequest {
    @NotBlank(message = "Crop type is required")
    private String cropType;

    @NotNull(message = "Area planted is required")
    @Min(value = 0, message = "Area planted must be positive")
    private Double areaPlanted;

    @NotNull(message = "Investment amount is required")
    @Min(value = 0, message = "Investment amount must be positive")
    private Long investmentAmount;

    private String season = "KHARIF"; // Default
}
