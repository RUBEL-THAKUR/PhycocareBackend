package com.psychocare.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddMoneyRequest {
    @NotNull @Min(1)
    private Double amount;
    private String paymentMethod;
    private String referenceId;
}
