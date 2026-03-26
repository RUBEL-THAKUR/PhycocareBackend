package com.psychocare.backend.config;

import com.psychocare.backend.model.Admin;
import com.psychocare.backend.model.AppUser;
import com.psychocare.backend.model.Reward;
import com.psychocare.backend.repository.AdminRepository;
import com.psychocare.backend.repository.AppUserRepository;
import com.psychocare.backend.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final AppUserRepository userRepository;
    private final RewardRepository rewardRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedDemoUser();
        seedRewards();
    }

    private void seedAdmin() {
        if (adminRepository.existsByEmail("admin@psychocare.com")) {
            return;
        }
        Admin admin = Admin.builder()
                .name("PsychoCare Admin")
                .email("admin@psychocare.com")
                .password(passwordEncoder.encode("Admin@1234"))
                .build();
        adminRepository.save(admin);
        log.info("Default admin seeded -> admin@psychocare.com / Admin@1234");
    }

    private void seedDemoUser() {
        if (userRepository.existsByEmailId("user@psychocare.com")) {
            return;
        }
        AppUser user = AppUser.builder()
                .firstName("Demo")
                .lastName("User")
                .emailId("user@psychocare.com")
                .username("demouser")
                .mobileNumber("9000000001")
                .password(passwordEncoder.encode("User@1234"))
                .referralCode("1WNYZOBJA0")
                .walletBalance(500.0)
                .active(true)
                .deleted(false)
                .build();
        userRepository.save(user);
        log.info("Demo user seeded -> user@psychocare.com / User@1234");
    }

    private void seedRewards() {
        if (rewardRepository.count() > 0) {
            return;
        }
        Reward reward = Reward.builder()
                .promoCode("ANGEL2200")
                .description("Happinetz Reward - Special discount for PsychoCare users")
                .discountValue(200.0)
                .discountType("FLAT")
                .partnerName("Happinetz")
                .terms("Valid for first-time Happinetz users. One-time use per account. Cannot be combined with other offers.")
                .validTill(LocalDate.now().plusYears(1))
                .isActive(true)
                .build();
        rewardRepository.save(reward);
        log.info("Default reward seeded -> ANGEL2200");
    }
}
