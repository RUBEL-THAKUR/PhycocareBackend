package com.psychocare.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TherapistSignupRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Email @NotBlank
    private String emailId;
    @NotBlank
    private String password;
    @NotBlank
    private String mobileNumber;
    private boolean isAbove18;
    private boolean acceptedTerms;
}
