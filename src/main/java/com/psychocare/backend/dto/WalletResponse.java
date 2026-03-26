package com.psychocare.backend.dto;

import com.psychocare.backend.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WalletResponse {
    private Double balance;
    private Long userId;
}
