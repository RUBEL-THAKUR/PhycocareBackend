package com.psychocare.backend.repository;

import com.psychocare.backend.model.TherapistExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TherapistExperienceRepository extends JpaRepository<TherapistExperience, Long> {
    List<TherapistExperience> findByTherapistId(UUID therapistId);
    void deleteByTherapistId(UUID therapistId);
}
