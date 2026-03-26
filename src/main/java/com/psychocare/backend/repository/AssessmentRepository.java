package com.psychocare.backend.repository;

import com.psychocare.backend.model.Assessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByUserUserIdAndCompleted(Long userId, Boolean completed);
    Page<Assessment> findByUserUserId(Long userId, Pageable pageable);
}
