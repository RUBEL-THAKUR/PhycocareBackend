package com.psychocare.backend.repository;

import com.psychocare.backend.model.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Page<Prescription> findByUserUserId(Long userId, Pageable pageable);
    Page<Prescription> findByTherapistId(UUID therapistId, Pageable pageable);
}
