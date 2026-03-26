package com.psychocare.backend.dto;

import com.psychocare.backend.model.SessionMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookSessionRequest {
    @NotNull
    private UUID therapistId;
    @NotNull
    private SessionMode mode;
    @NotNull
    private LocalDateTime scheduledAt;
    private Integer durationMinutes = 60;
}
