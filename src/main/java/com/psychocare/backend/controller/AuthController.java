package com.psychocare.backend.controller;

import com.psychocare.backend.dto.*;
import com.psychocare.backend.service.OtpService;
import com.psychocare.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final OtpService otpService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserLoginResponse>> signup(@Valid @RequestBody UserSignupRequest request) {
        UserLoginResponse response = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok("Account created successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.loginWithPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/request-otp")
    public ResponseEntity<ApiResponse<Void>> requestOtp(@RequestBody OtpRequest request) {
        otpService.generateAndSendOtp(request.getIdentifier());
        return ResponseEntity.ok(ApiResponse.ok("OTP sent to " + request.getIdentifier()));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<UserLoginResponse>> verifyOtp(@RequestBody OtpVerifyRequest request) {
        UserLoginResponse response = userService.loginWithOtp(request.getIdentifier(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }
}
