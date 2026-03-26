package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QualificationRequest {
    @NotBlank private String degree;
    @NotBlank private String institution;
    @NotNull private Integer passingYear;
    private String specialization;
}
