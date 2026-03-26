package com.psychocare.backend.service;

import com.psychocare.backend.dto.BookSessionRequest;
import com.psychocare.backend.dto.SessionResponse;
import com.psychocare.backend.exception.BadRequestException;
import com.psychocare.backend.exception.ResourceNotFoundException;
import com.psychocare.backend.model.*;
import com.psychocare.backend.repository.SessionRepository;
import com.psychocare.backend.repository.TherapistProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final TherapistProfileRepository profileRepository;
    private final TherapistService therapistService;
    private final UserService userService;

    @Transactional
    public Session bookSession(Long userId, BookSessionRequest request) {
        AppUser user = userService.getById(userId);
        Therapist therapist = therapistService.getById(request.getTherapistId());

        if (therapist.getStatus() != TherapistStatus.APPROVED) {
            throw new BadRequestException("Therapist is not available for booking");
        }

        // Determine fee based on mode
        double fee = resolveFee(therapist.getId(), request.getMode(), therapist.getConsultationFee());

        // Deduct from wallet
        userService.deductWallet(user, fee,
                "Session booked with " + therapist.getFirstName() + " " + therapist.getLastName()
                + " (" + request.getMode().name() + ")");

        String roomId = "room-" + UUID.randomUUID().toString().substring(0, 8);

        Session session = Session.builder()
                .user(user)
                .therapist(therapist)
                .mode(request.getMode())
                .status(SessionStatus.UPCOMING)
                .scheduledAt(request.getScheduledAt())
                .durationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60)
                .amountPaid(fee)
                .roomId(roomId)
                .build();

        Session saved = sessionRepository.save(session);

        // Process referral credit if this is the user's first session
        long totalSessions = sessionRepository.countByUser(userId);
        if (totalSessions == 1) {
            userService.processReferralCreditOnFirstSession(user);
        }

        return saved;
    }

    @Transactional
    public Session cancelByUser(Long sessionId, Long userId, String reason) {
        Session session = getById(sessionId);

        if (!session.getUser().getUserId().equals(userId)) {
            throw new BadRequestException("You are not authorized to cancel this session");
        }
        if (session.getStatus() != SessionStatus.UPCOMING) {
            throw new BadRequestException("Only upcoming sessions can be cancelled");
        }
        if (session.getScheduledAt().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Sessions cannot be cancelled within 2 hours of start time");
        }

        session.setStatus(SessionStatus.CANCELLED_BY_USER);
        session.setCancellationReason(reason);
        session.setCancelledAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Refund wallet
        userService.refundWallet(session.getUser(), session.getAmountPaid(),
                "Refund for cancelled session #" + sessionId);

        return session;
    }

    @Transactional
    public Session cancelByTherapist(Long sessionId, UUID therapistId, String reason) {
        Session session = getById(sessionId);

        if (!session.getTherapist().getId().equals(therapistId)) {
            throw new BadRequestException("You are not authorized to cancel this session");
        }
        if (session.getStatus() != SessionStatus.UPCOMING) {
            throw new BadRequestException("Only upcoming sessions can be cancelled");
        }

        session.setStatus(SessionStatus.CANCELLED_BY_THERAPIST);
        session.setCancellationReason(reason);
        session.setCancelledAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Full refund to user
        userService.refundWallet(session.getUser(), session.getAmountPaid(),
                "Refund - therapist cancelled session #" + sessionId);

        return session;
    }

    @Transactional
    public Session markCompleted(Long sessionId, UUID therapistId) {
        Session session = getById(sessionId);
        if (!session.getTherapist().getId().equals(therapistId)) {
            throw new BadRequestException("Not authorized");
        }
        session.setStatus(SessionStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());

        // Increment therapist total sessions
        Therapist therapist = session.getTherapist();
        therapist.setTotalSessions(therapist.getTotalSessions() + 1);
        therapistService.getById(therapistId); // just to ensure it loads — save happens through cascade

        return sessionRepository.save(session);
    }

    public Session getById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + sessionId));
    }

//    public Page<Session> getUserSessions(Long userId, SessionStatus status, Pageable pageable) {
//        if (status != null) {
//            return sessionRepository.findByUserUserIdAndStatus(userId, status, pageable);
//        }
//        return sessionRepository.findByUserUserId(userId, pageable);
//    }
//
//    public Page<Session> getTherapistSessions(UUID therapistId, SessionStatus status, Pageable pageable) {
//        if (status != null) {
//            return sessionRepository.findByTherapistIdAndStatus(therapistId, status, pageable);
//        }
//        return sessionRepository.findByTherapistId(therapistId, pageable);
//    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getUserSessions(Long userId, SessionStatus status, Pageable pageable) {
        if (status != null) {
            return sessionRepository.findByUserUserIdAndStatus(userId, status, pageable)
                    .map(this::toResponse);
        }
        return sessionRepository.findByUserUserId(userId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SessionResponse> getTherapistSessions(UUID therapistId, SessionStatus status, Pageable pageable) {
        if (status != null) {
            return sessionRepository.findByTherapistIdAndStatus(therapistId, status, pageable)
                    .map(this::toResponse);
        }
        return sessionRepository.findByTherapistId(therapistId, pageable)
                .map(this::toResponse);
    }

    // Runs every 15 minutes — marks sessions as EXPIRED if they are still UPCOMING past schedule + duration
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void expireOldSessions() {
        List<Session> upcoming = sessionRepository.findByStatusAndScheduledAtBefore(
                SessionStatus.UPCOMING,
                LocalDateTime.now().minusHours(2)
        );
        for (Session session : upcoming) {
            session.setStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            log.info("Session {} marked as EXPIRED", session.getId());
        }
    }

    public SessionResponse toResponse(Session s) {
        return SessionResponse.builder()
                .id(s.getId())
                .userId(s.getUser().getUserId())
                .userName(s.getUser().getFirstName() + " " + s.getUser().getLastName())
                .therapistId(s.getTherapist().getId().toString())
                .therapistName(s.getTherapist().getFirstName() + " " + s.getTherapist().getLastName())
                .mode(s.getMode())
                .status(s.getStatus())
                .scheduledAt(s.getScheduledAt())
                .durationMinutes(s.getDurationMinutes())
                .amountPaid(s.getAmountPaid())
                .roomId(s.getRoomId())
                .cancellationReason(s.getCancellationReason())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private double resolveFee(UUID therapistId, SessionMode mode, Double fallback) {
        TherapistProfile profile = profileRepository.findByTherapistId(therapistId).orElse(null);
        if (profile != null) {
            return switch (mode) {
                case CHAT -> profile.getConsultationFeeChat() != null ? profile.getConsultationFeeChat() : orFallback(fallback);
                case AUDIO -> profile.getConsultationFeeAudio() != null ? profile.getConsultationFeeAudio() : orFallback(fallback);
                case VIDEO -> profile.getConsultationFeeVideo() != null ? profile.getConsultationFeeVideo() : orFallback(fallback);
            };
        }
        return orFallback(fallback);
    }

    private double orFallback(Double fee) {
        if (fee == null || fee <= 0) throw new BadRequestException("Therapist fee not configured for this session mode");
        return fee;
    }
}
