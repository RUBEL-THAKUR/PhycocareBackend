package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssessmentSubmitRequest {
    @NotBlank
    private String testName;
    @NotBlank
    private String testSlug;
    @NotNull
    private Integer score;
    private String resultLabel;
    private String answers;
}
