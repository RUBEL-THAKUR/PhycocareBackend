package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExperienceRequest {
    @NotBlank private String organization;
    @NotBlank private String designation;
    @NotNull private Integer fromYear;
    private Integer toYear;
    private boolean current;
}
