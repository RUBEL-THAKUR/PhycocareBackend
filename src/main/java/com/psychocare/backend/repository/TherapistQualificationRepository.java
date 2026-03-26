package com.psychocare.backend.repository;

import com.psychocare.backend.model.TherapistQualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TherapistQualificationRepository extends JpaRepository<TherapistQualification, Long> {
    List<TherapistQualification> findByTherapistId(UUID therapistId);
    void deleteByTherapistId(UUID therapistId);
}
