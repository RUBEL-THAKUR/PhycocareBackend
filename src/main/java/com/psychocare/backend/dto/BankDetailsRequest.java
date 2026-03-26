package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BankDetailsRequest {
    @NotBlank private String accountHolderName;
    @NotBlank private String accountNumber;
    @NotBlank private String ifscCode;
    @NotBlank private String bankName;
    private String branchName;
    private String upiId;
}
