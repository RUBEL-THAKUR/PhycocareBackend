package com.psychocare.backend.repository;

import com.psychocare.backend.model.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpEntity, Long> {
    Optional<OtpEntity> findTopByIdentifierAndIsUsedFalseOrderByCreatedAtDesc(String identifier);
    void deleteAllByIdentifier(String identifier);
}
