package com.psychocare.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserSignupRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email
    @NotBlank
    private String emailId;
    @Size(min = 10, max = 15)
    private String mobileNumber;
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 6)
    private String password;
    private String referralCode;
}
