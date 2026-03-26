package com.psychocare.backend.repository;

import com.psychocare.backend.model.TherapistSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TherapistSpecializationRepository extends JpaRepository<TherapistSpecialization, Long> {
    List<TherapistSpecialization> findByTherapistId(UUID therapistId);
    void deleteByTherapistId(UUID therapistId);
}
