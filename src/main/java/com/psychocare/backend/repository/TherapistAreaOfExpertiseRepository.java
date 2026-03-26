package com.psychocare.backend.repository;

import com.psychocare.backend.model.TherapistAreaOfExpertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TherapistAreaOfExpertiseRepository extends JpaRepository<TherapistAreaOfExpertise, Long> {
    List<TherapistAreaOfExpertise> findByTherapistId(UUID therapistId);
    void deleteByTherapistId(UUID therapistId);
}
