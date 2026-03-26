package com.psychocare.backend.service;

import com.psychocare.backend.dto.AdminLoginRequest;
import com.psychocare.backend.exception.UnauthorizedException;
import com.psychocare.backend.model.Admin;
import com.psychocare.backend.repository.AdminRepository;
import com.psychocare.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public Map<String, Object> login(AdminLoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateAdminToken(admin.getId(), admin.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("adminId", admin.getId());
        response.put("name", admin.getName());
        response.put("email", admin.getEmail());
        return response;
    }
}
