package com.psychocare.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private String senderName;
    private String recipientEmail;
    private String subject;
    private String body;
    private String folder;
    private Boolean isRead;
    private Boolean isStarred;
    private Boolean isImportant;
    private LocalDateTime createdAt;
}
