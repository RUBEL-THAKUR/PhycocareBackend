package com.psychocare.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequest {
    @NotNull @Min(1) @Max(5)
    private Integer qualityRating;
    @NotNull @Min(1) @Max(5)
    private Integer helpfulnessRating;
    @NotNull @Min(1) @Max(5)
    private Integer clarityRating;
    private String source;
    private String comment;
    private Boolean isAnonymous = false;
}
