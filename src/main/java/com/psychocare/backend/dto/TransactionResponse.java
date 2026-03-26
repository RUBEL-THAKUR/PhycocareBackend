package com.psychocare.backend.dto;

import com.psychocare.backend.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private Long id;
    private TransactionType type;
    private Double amount;
    private Double balanceAfter;
    private String description;
    private String referenceId;
    private LocalDateTime createdAt;
}
