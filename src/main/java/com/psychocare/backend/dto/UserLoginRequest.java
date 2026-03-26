package com.psychocare.backend.dto;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String emailId;
    private String mobileNumber;
    private String password;
    private String otp;
}
