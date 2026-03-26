package com.psychocare.backend.controller;

import com.psychocare.backend.dto.ApiResponse;
import com.psychocare.backend.dto.TherapistPublicResponse;
import com.psychocare.backend.model.TherapistStatus;
import com.psychocare.backend.service.TherapistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/therapists/public")
@RequiredArgsConstructor
public class PublicTherapistController {

    private final TherapistService therapistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TherapistPublicResponse>>> listApproved(
            @RequestParam(required = false) String search) {
        List<TherapistPublicResponse> list = therapistService.getByStatus(TherapistStatus.APPROVED)
                .stream()
                .map(therapistService::toPublicResponse)
                .filter(t -> search == null || search.isBlank()
                        || (t.getFirstName() + " " + t.getLastName()).toLowerCase().contains(search.toLowerCase())
                        || (t.getCategory() != null && t.getCategory().toLowerCase().contains(search.toLowerCase()))
                        || t.getSpecializations().stream().anyMatch(s -> s.toLowerCase().contains(search.toLowerCase())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TherapistPublicResponse>> getOne(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(therapistService.toPublicResponse(therapistService.getById(id))));
    }
}
