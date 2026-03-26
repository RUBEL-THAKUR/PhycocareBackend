package com.psychocare.backend.repository;

import com.psychocare.backend.model.TherapistBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TherapistBankDetailsRepository extends JpaRepository<TherapistBankDetails, Long> {
    Optional<TherapistBankDetails> findByTherapistId(UUID therapistId);
}
