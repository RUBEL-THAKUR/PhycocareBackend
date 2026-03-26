package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AreaOfExpertiseRequest {
    @NotBlank private String areaName;
}
