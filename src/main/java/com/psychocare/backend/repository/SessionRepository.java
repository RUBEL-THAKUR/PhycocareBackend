package com.psychocare.backend.repository;

import com.psychocare.backend.model.Session;
import com.psychocare.backend.model.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
//    Page<Session> findByUserUserId(Long userId, Pageable pageable);
//    Page<Session> findByUserUserIdAndStatus(Long userId, SessionStatus status, Pageable pageable);
//    Page<Session> findByTherapistId(UUID therapistId, Pageable pageable);
//    Page<Session> findByTherapistIdAndStatus(UUID therapistId, SessionStatus status, Pageable pageable);
@Query("SELECT s FROM Session s JOIN FETCH s.user JOIN FETCH s.therapist WHERE s.therapist.id = :therapistId")
Page<Session> findByTherapistId(@Param("therapistId") UUID therapistId, Pageable pageable);

    @Query("SELECT s FROM Session s JOIN FETCH s.user JOIN FETCH s.therapist WHERE s.therapist.id = :therapistId AND s.status = :status")
    Page<Session> findByTherapistIdAndStatus(@Param("therapistId") UUID therapistId, @Param("status") SessionStatus status, Pageable pageable);

    @Query("SELECT s FROM Session s JOIN FETCH s.user JOIN FETCH s.therapist WHERE s.user.userId = :userId")
    Page<Session> findByUserUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT s FROM Session s JOIN FETCH s.user JOIN FETCH s.therapist WHERE s.user.userId = :userId AND s.status = :status")
    Page<Session> findByUserUserIdAndStatus(@Param("userId") Long userId, @Param("status") SessionStatus status, Pageable pageable);
    List<Session> findByStatusAndScheduledAtBefore(SessionStatus status, LocalDateTime cutoff);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.therapist.id = :therapistId AND s.status = 'COMPLETED'")
    long countCompletedByTherapist(UUID therapistId);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.userId = :userId")
    long countByUser(Long userId);
}
