package com.psychocare.backend.dto;

import com.psychocare.backend.model.SessionMode;
import com.psychocare.backend.model.SessionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String therapistId;
    private String therapistName;
    private SessionMode mode;
    private SessionStatus status;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private Double amountPaid;
    private String roomId;
    private String cancellationReason;
    private LocalDateTime createdAt;
}
