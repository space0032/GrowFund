package com.growfund.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantCropRequest {
    @NotBlank(message = "Crop type is required")
    private String cropType;

    @NotNull(message = "Area planted is required")
    @Min(value = 0, message = "Area planted must be positive")
    private Double areaPlanted;

    private String season = "KHARIF"; // Default
}
