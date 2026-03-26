package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AwardRequest {
    @NotBlank private String awardName;
    private String awardingBody;
    private Integer year;
}
