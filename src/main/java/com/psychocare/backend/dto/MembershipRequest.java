package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MembershipRequest {
    @NotBlank private String organizationName;
    private String membershipId;
    private String validTill;
}
