package com.psychocare.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank @Size(min = 6)
    private String newPassword;
    @NotBlank
    private String confirmPassword;
}
