package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComposeMessageRequest {
    @NotBlank
    private String recipientEmail;
    @NotBlank
    private String subject;
    @NotBlank
    private String body;
}
