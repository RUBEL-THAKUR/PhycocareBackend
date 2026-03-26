package com.psychocare.backend.service;

import com.psychocare.backend.dto.PrescriptionRequest;
import com.psychocare.backend.exception.BadRequestException;
import com.psychocare.backend.model.Prescription;
import com.psychocare.backend.model.Session;
import com.psychocare.backend.model.SessionStatus;
import com.psychocare.backend.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final SessionService sessionService;

    @Transactional
    public Prescription create(UUID therapistId, PrescriptionRequest request) {
        Session session = sessionService.getById(request.getSessionId());

        if (!session.getTherapist().getId().equals(therapistId)) {
            throw new BadRequestException("You are not the therapist for this session");
        }
        if (session.getStatus() != SessionStatus.COMPLETED) {
            throw new BadRequestException("Prescriptions can only be added for completed sessions");
        }

        Prescription prescription = Prescription.builder()
                .session(session)
                .user(session.getUser())
                .therapist(session.getTherapist())
                .notes(request.getNotes())
                .medications(request.getMedications())
                .followUpDate(request.getFollowUpDate())
                .build();

        return prescriptionRepository.save(prescription);
    }

    public Page<Prescription> getUserPrescriptions(Long userId, Pageable pageable) {
        return prescriptionRepository.findByUserUserId(userId, pageable);
    }

    public Page<Prescription> getTherapistPrescriptions(UUID therapistId, Pageable pageable) {
        return prescriptionRepository.findByTherapistId(therapistId, pageable);
    }
}
