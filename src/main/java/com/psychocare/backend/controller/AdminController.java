package com.psychocare.backend.controller;

import com.psychocare.backend.dto.AdminLoginRequest;
import com.psychocare.backend.dto.ApiResponse;
import com.psychocare.backend.model.AppUser;
import com.psychocare.backend.model.Therapist;
import com.psychocare.backend.model.TherapistStatus;
import com.psychocare.backend.repository.AppUserRepository;
import com.psychocare.backend.repository.SessionRepository;
import com.psychocare.backend.service.AdminService;
import com.psychocare.backend.service.TherapistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final TherapistService therapistService;
    private final AppUserRepository userRepository;
    private final SessionRepository sessionRepository;

    // ---- Auth ----

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody AdminLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", adminService.login(request)));
    }

    // ---- Dashboard Stats ----

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalTherapists", therapistService.getAll().size());
        stats.put("pendingTherapists", therapistService.getByStatus(TherapistStatus.PENDING).size());
        stats.put("approvedTherapists", therapistService.getByStatus(TherapistStatus.APPROVED).size());
        stats.put("totalSessions", sessionRepository.count());
        return ResponseEntity.ok(ApiResponse.ok("Stats retrieved", stats));
    }

    // ---- Therapist Management ----

    @GetMapping("/therapists")
    public ResponseEntity<ApiResponse<List<Therapist>>> getAllTherapists(
            @RequestParam(required = false) TherapistStatus status) {
        List<Therapist> list = status != null
                ? therapistService.getByStatus(status)
                : therapistService.getAll();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/therapists/{id}")
    public ResponseEntity<ApiResponse<Therapist>> getTherapist(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getById(id)));
    }

    @PutMapping("/therapists/{id}/approve")
    public ResponseEntity<ApiResponse<Therapist>> approveTherapist(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Therapist approved", therapistService.approve(id, "ADMIN")));
    }

    @PutMapping("/therapists/{id}/reject")
    public ResponseEntity<ApiResponse<Therapist>> rejectTherapist(
            @PathVariable UUID id,
            @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.ok("Therapist rejected", therapistService.reject(id, reason)));
    }

    // ---- User Management ----

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AppUser>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.ok(userRepository.findAll()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<AppUser>> getUser(@PathVariable Long id) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new com.psychocare.backend.exception.ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
