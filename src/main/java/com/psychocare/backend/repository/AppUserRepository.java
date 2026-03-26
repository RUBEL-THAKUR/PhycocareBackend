package com.psychocare.backend.repository;

import com.psychocare.backend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailId(String emailId);
    Optional<AppUser> findByMobileNumber(String mobileNumber);
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByReferralCode(String referralCode);
    boolean existsByEmailId(String emailId);
    boolean existsByMobileNumber(String mobileNumber);
    boolean existsByUsername(String username);
}
