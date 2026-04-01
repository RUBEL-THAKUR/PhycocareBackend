package com.psychocare.backend.repository;

import com.psychocare.backend.model.Session;
import com.psychocare.backend.model.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    // ===============================
    // EAGER FETCH BY ID (NEW ✅)
    // ===============================

    @EntityGraph(attributePaths = {"user", "therapist"})
    @Query("SELECT s FROM Session s WHERE s.id = :id")
    Optional<Session> findWithAssociationsById(@Param("id") Long id);

    // ===============================
    // USER SESSION APIs
    // ===============================

    @EntityGraph(attributePaths = {"user", "therapist"})
    Page<Session> findByUserUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "therapist"})
    Page<Session> findByUserUserIdAndStatus(Long userId, SessionStatus status, Pageable pageable);


    // ===============================
    // THERAPIST SESSION APIs
    // ===============================

    @EntityGraph(attributePaths = {"user", "therapist"})
    Page<Session> findByTherapistId(UUID therapistId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "therapist"})
    Page<Session> findByTherapistIdAndStatus(UUID therapistId, SessionStatus status, Pageable pageable);


    // ===============================
    // SCHEDULER SUPPORT
    // ===============================

    List<Session> findByStatusAndScheduledAtBefore(SessionStatus status, LocalDateTime cutoff);


    // ===============================
    // ANALYTICS
    // ===============================

    long countByUserUserId(Long userId);

    long countByTherapistIdAndStatus(UUID therapistId, SessionStatus status);
}