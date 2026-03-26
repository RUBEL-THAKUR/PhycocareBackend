package com.psychocare.backend.repository;

import com.psychocare.backend.model.Referral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Page<Referral> findByReferrerUserId(Long userId, Pageable pageable);
    boolean existsByReferralCodeAndReferredUserUserId(String code, Long userId);
}
