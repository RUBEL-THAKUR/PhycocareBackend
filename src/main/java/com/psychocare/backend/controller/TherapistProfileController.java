package com.psychocare.backend.controller;

import com.psychocare.backend.dto.*;
import com.psychocare.backend.model.*;
import com.psychocare.backend.security.JwtUtil;
import com.psychocare.backend.service.PrescriptionService;
import com.psychocare.backend.service.SessionService;
import com.psychocare.backend.service.TherapistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/therapist/profile")
@RequiredArgsConstructor
public class TherapistProfileController {

    private final TherapistService therapistService;
    private final SessionService sessionService;
    private final PrescriptionService prescriptionService;
    private final JwtUtil jwtUtil;

    // ---- Basic Profile ----

    @PostMapping("/basic-info")
    public ResponseEntity<ApiResponse<TherapistProfile>> saveProfile(
            @RequestHeader("Authorization") String auth,
            @RequestBody TherapistProfileRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Profile saved", therapistService.saveProfile(id, request)));
    }

    @GetMapping("/basic-info")
    public ResponseEntity<ApiResponse<TherapistProfile>> getProfile(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getProfile(id)));
    }

    // ---- Qualifications ----

    @PostMapping("/qualifications")
    public ResponseEntity<ApiResponse<TherapistQualification>> addQualification(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody QualificationRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Qualification added", therapistService.addQualification(id, request)));
    }

    @GetMapping("/qualifications")
    public ResponseEntity<ApiResponse<List<TherapistQualification>>> getQualifications(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getQualifications(id)));
    }

    @DeleteMapping("/qualifications/{qId}")
    public ResponseEntity<ApiResponse<Void>> deleteQualification(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long qId) {
        therapistService.deleteQualification(qId);
        return ResponseEntity.ok(ApiResponse.ok("Qualification removed"));
    }

    // ---- Experience ----

    @PostMapping("/experience")
    public ResponseEntity<ApiResponse<TherapistExperience>> addExperience(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody ExperienceRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Experience added", therapistService.addExperience(id, request)));
    }

    @GetMapping("/experience")
    public ResponseEntity<ApiResponse<List<TherapistExperience>>> getExperiences(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getExperiences(id)));
    }

    @DeleteMapping("/experience/{eId}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long eId) {
        therapistService.deleteExperience(eId);
        return ResponseEntity.ok(ApiResponse.ok("Experience removed"));
    }

    // ---- Specializations ----

    @PostMapping("/specializations")
    public ResponseEntity<ApiResponse<TherapistSpecialization>> addSpecialization(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody SpecializationRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Specialization added", therapistService.addSpecialization(id, request)));
    }

    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<TherapistSpecialization>>> getSpecializations(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getSpecializations(id)));
    }

    @DeleteMapping("/specializations/{sId}")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialization(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long sId) {
        therapistService.deleteSpecialization(sId);
        return ResponseEntity.ok(ApiResponse.ok("Specialization removed"));
    }

    // ---- Areas of Expertise ----

    @PostMapping("/expertise")
    public ResponseEntity<ApiResponse<TherapistAreaOfExpertise>> addExpertise(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody AreaOfExpertiseRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Area of expertise added", therapistService.addAreaOfExpertise(id, request)));
    }

    @GetMapping("/expertise")
    public ResponseEntity<ApiResponse<List<TherapistAreaOfExpertise>>> getExpertise(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getAreasOfExpertise(id)));
    }

    @DeleteMapping("/expertise/{aId}")
    public ResponseEntity<ApiResponse<Void>> deleteExpertise(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long aId) {
        therapistService.deleteAreaOfExpertise(aId);
        return ResponseEntity.ok(ApiResponse.ok("Area of expertise removed"));
    }

    // ---- Awards ----

    @PostMapping("/awards")
    public ResponseEntity<ApiResponse<TherapistAward>> addAward(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody AwardRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Award added", therapistService.addAward(id, request)));
    }

    @GetMapping("/awards")
    public ResponseEntity<ApiResponse<List<TherapistAward>>> getAwards(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getAwards(id)));
    }

    @DeleteMapping("/awards/{aId}")
    public ResponseEntity<ApiResponse<Void>> deleteAward(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long aId) {
        therapistService.deleteAward(aId);
        return ResponseEntity.ok(ApiResponse.ok("Award removed"));
    }

    // ---- Memberships ----

    @PostMapping("/memberships")
    public ResponseEntity<ApiResponse<TherapistProfessionalMembership>> addMembership(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody MembershipRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Membership added", therapistService.addMembership(id, request)));
    }

    @GetMapping("/memberships")
    public ResponseEntity<ApiResponse<List<TherapistProfessionalMembership>>> getMemberships(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getMemberships(id)));
    }

    @DeleteMapping("/memberships/{mId}")
    public ResponseEntity<ApiResponse<Void>> deleteMembership(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long mId) {
        therapistService.deleteMembership(mId);
        return ResponseEntity.ok(ApiResponse.ok("Membership removed"));
    }

    // ---- Bank Details ----

    @PostMapping("/bank-details")
    public ResponseEntity<ApiResponse<TherapistBankDetails>> saveBankDetails(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody BankDetailsRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Bank details saved", therapistService.saveBankDetails(id, request)));
    }

    @GetMapping("/bank-details")
    public ResponseEntity<ApiResponse<TherapistBankDetails>> getBankDetails(
            @RequestHeader("Authorization") String auth) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(therapistService.getBankDetails(id)));
    }

    // ---- Sessions ----

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<SessionResponse>>> getSessions(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) SessionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID id = extractTherapistId(auth);
//        Page<SessionResponse> sessions = sessionService.getTherapistSessions(
//                id, status, PageRequest.of(page, size, Sort.by("scheduledAt").descending()))
//                .map(sessionService::toResponse);
        Page<SessionResponse> sessions = sessionService.getTherapistSessions(
                id, status, PageRequest.of(page, size, Sort.by("scheduledAt").descending()));
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<ApiResponse<SessionResponse>> completeSession(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long sessionId) {
        UUID id = extractTherapistId(auth);
        Session session = sessionService.markCompleted(sessionId, id);
        return ResponseEntity.ok(ApiResponse.ok("Session marked as completed", sessionService.toResponse(session)));
    }

    @PostMapping("/sessions/{sessionId}/cancel")
    public ResponseEntity<ApiResponse<SessionResponse>> cancelSession(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long sessionId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        UUID id = extractTherapistId(auth);
        Session session = sessionService.cancelByTherapist(sessionId, id, reason);
        return ResponseEntity.ok(ApiResponse.ok("Session cancelled", sessionService.toResponse(session)));
    }

    // ---- Prescriptions ----

    @PostMapping("/prescriptions")
    public ResponseEntity<ApiResponse<Prescription>> createPrescription(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody PrescriptionRequest request) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Prescription created", prescriptionService.create(id, request)));
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<ApiResponse<Page<Prescription>>> getPrescriptions(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UUID id = extractTherapistId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                prescriptionService.getTherapistPrescriptions(id, PageRequest.of(page, size))));
    }

    private UUID extractTherapistId(String authHeader) {
        return jwtUtil.extractTherapistId(authHeader.substring(7));
    }
}
