package com.psychocare.backend.dto;

import lombok.Data;

@Data
public class TherapistLoginRequest {
    private String emailId;
    private String password;
}
