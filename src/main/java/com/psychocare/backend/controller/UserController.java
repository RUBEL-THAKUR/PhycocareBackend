package com.psychocare.backend.controller;

import com.psychocare.backend.dto.*;
import com.psychocare.backend.model.*;
import com.psychocare.backend.repository.ReferralRepository;
import com.psychocare.backend.repository.WalletTransactionRepository;
import com.psychocare.backend.security.JwtUtil;
import com.psychocare.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;
    private final FeedbackService feedbackService;
    private final MessageService messageService;
    private final AssessmentService assessmentService;
    private final PrescriptionService prescriptionService;
    private final RewardService rewardService;
    private final ReferralRepository referralRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final JwtUtil jwtUtil;

    // ---- Profile ----

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<AppUser>> getProfile(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(userService.getById(userId)));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<AppUser>> updateProfile(
            @RequestHeader("Authorization") String auth,
            @RequestBody UserProfileUpdateRequest request) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", userService.updateProfile(userId, request)));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody ChangePasswordRequest request) {
        Long userId = extractUserId(auth);
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Password changed successfully"));
    }

    // ---- Wallet ----

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<WalletResponse>> getWallet(@RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(userService.getWallet(userId)));
    }

    @PostMapping("/wallet/add")
    public ResponseEntity<ApiResponse<WalletResponse>> addMoney(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody AddMoneyRequest request) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Money added to wallet", userService.addMoney(userId, request)));
    }

    @GetMapping("/wallet/transactions")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TransactionResponse> transactions = walletTransactionRepository
                .findByUserUserId(userId, pageable)
                .map(t -> TransactionResponse.builder()
                        .id(t.getId())
                        .type(t.getType())
                        .amount(t.getAmount())
                        .balanceAfter(t.getBalanceAfter())
                        .description(t.getDescription())
                        .referenceId(t.getReferenceId())
                        .createdAt(t.getCreatedAt())
                        .build());
        return ResponseEntity.ok(ApiResponse.ok(transactions));
    }

    // ---- Sessions ----

    @PostMapping("/sessions/book")
    public ResponseEntity<ApiResponse<SessionResponse>> bookSession(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody BookSessionRequest request) {
        Long userId = extractUserId(auth);
        Session session = sessionService.bookSession(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Session booked successfully", sessionService.toResponse(session)));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<SessionResponse>>> getSessions(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) SessionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledAt").descending());
//        Page<SessionResponse> sessions = sessionService.getUserSessions(userId, status, pageable)
//                .map(sessionService::toResponse);
        Page<SessionResponse> sessions = sessionService.getUserSessions(userId, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    @PostMapping("/sessions/{sessionId}/cancel")
    public ResponseEntity<ApiResponse<SessionResponse>> cancelSession(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long sessionId,
            @RequestParam(required = false, defaultValue = "") String reason) {
        Long userId = extractUserId(auth);
        Session session = sessionService.cancelByUser(sessionId, userId, reason);
        return ResponseEntity.ok(ApiResponse.ok("Session cancelled", sessionService.toResponse(session)));
    }

    // ---- Feedback ----

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Feedback>> submitFeedback(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody FeedbackRequest request) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Feedback submitted", feedbackService.submit(userId, request)));
    }

    // ---- Messages ----

    @PostMapping("/messages/compose")
    public ResponseEntity<ApiResponse<MessageResponse>> composeMessage(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody ComposeMessageRequest request) {
        Long userId = extractUserId(auth);
        Message msg = messageService.compose(userId, request);
        return ResponseEntity.ok(ApiResponse.ok("Message sent", messageService.toResponse(msg)));
    }

    @GetMapping("/messages/inbox")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getInbox(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Page<MessageResponse> messages = messageService.getInbox(userId, PageRequest.of(page, size))
                .map(messageService::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @GetMapping("/messages/sent")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getSent(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Page<MessageResponse> messages = messageService.getSent(userId, PageRequest.of(page, size))
                .map(messageService::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @GetMapping("/messages/starred")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getStarred(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Page<MessageResponse> messages = messageService.getStarred(userId, PageRequest.of(page, size))
                .map(messageService::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @GetMapping("/messages/important")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getImportant(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Page<MessageResponse> messages = messageService.getImportant(userId, PageRequest.of(page, size))
                .map(messageService::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @GetMapping("/messages/trash")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getTrash(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Page<MessageResponse> messages = messageService.getTrash(userId, PageRequest.of(page, size))
                .map(messageService::toResponse);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @PatchMapping("/messages/{id}/star")
    public ResponseEntity<ApiResponse<MessageResponse>> toggleStar(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(messageService.toResponse(messageService.toggleStar(id, userId))));
    }

    @PatchMapping("/messages/{id}/important")
    public ResponseEntity<ApiResponse<MessageResponse>> toggleImportant(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(messageService.toResponse(messageService.toggleImportant(id, userId))));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @RequestHeader("Authorization") String auth,
            @PathVariable Long id) {
        Long userId = extractUserId(auth);
        messageService.moveToTrash(id, userId);
        return ResponseEntity.ok(ApiResponse.ok("Message moved to trash"));
    }

    // ---- Assessments ----

    @PostMapping("/assessments")
    public ResponseEntity<ApiResponse<Assessment>> submitAssessment(
            @RequestHeader("Authorization") String auth,
            @Valid @RequestBody AssessmentSubmitRequest request) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok("Assessment saved", assessmentService.submit(userId, request)));
    }

    @GetMapping("/assessments/completed")
    public ResponseEntity<ApiResponse<List<Assessment>>> getCompletedAssessments(
            @RequestHeader("Authorization") String auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(assessmentService.getCompleted(userId)));
    }

    // ---- Prescriptions ----

    @GetMapping("/prescriptions")
    public ResponseEntity<ApiResponse<Page<Prescription>>> getPrescriptions(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(
                prescriptionService.getUserPrescriptions(userId, PageRequest.of(page, size))));
    }

    // ---- Referrals ----

    @GetMapping("/referrals")
    public ResponseEntity<ApiResponse<Page<Referral>>> getReferrals(
            @RequestHeader("Authorization") String auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = extractUserId(auth);
        Pageable pageable = PageRequest.of(page, size, Sort.by("usedOn").descending());
        return ResponseEntity.ok(ApiResponse.ok(referralRepository.findByReferrerUserId(userId, pageable)));
    }

    // ---- Rewards ----

    @GetMapping("/rewards")
    public ResponseEntity<ApiResponse<List<Reward>>> getRewards() {
        return ResponseEntity.ok(ApiResponse.ok(rewardService.getActiveRewards()));
    }

    // ---- Helper ----

    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}
