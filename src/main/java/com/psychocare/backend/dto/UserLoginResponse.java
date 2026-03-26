package com.psychocare.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResponse {
    private String token;
    private Long userId;
    private String firstName;
    private String lastName;
    private String emailId;
    private String username;
    private Double walletBalance;
    private String referralCode;
    private String profileImage;
}
