package com.psychocare.backend.repository;

import com.psychocare.backend.model.Therapist;
import com.psychocare.backend.model.TherapistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, UUID> {
    Optional<Therapist> findByEmailId(String emailId);
    List<Therapist> findByStatus(TherapistStatus status);
    boolean existsByEmailId(String emailId);
    boolean existsByMobileNumber(String mobileNumber);
}
