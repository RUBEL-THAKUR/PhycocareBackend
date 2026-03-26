package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrescriptionRequest {
    @NotNull private Long sessionId;
    private String notes;
    private String medications;
    private LocalDateTime followUpDate;
}
