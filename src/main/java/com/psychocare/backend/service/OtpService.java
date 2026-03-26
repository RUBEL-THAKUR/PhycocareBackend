package com.psychocare.backend.service;

import com.psychocare.backend.model.OtpEntity;
import com.psychocare.backend.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;

    private static final int OTP_EXPIRY_MINUTES = 10;

    @Transactional
    public void generateAndSendOtp(String identifier) {
        // Invalidate all existing OTPs for this identifier
        otpRepository.deleteAllByIdentifier(identifier);

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        OtpEntity entity = OtpEntity.builder()
                .identifier(identifier)
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .isUsed(false)
                .build();

        otpRepository.save(entity);

        boolean isEmail = identifier.contains("@");
        if (isEmail) {
            sendEmailOtp(identifier, otp);
        } else {
            log.info("SMS OTP for {}: {} (SMS integration not configured)", identifier, otp);
        }
    }

    @Transactional
    public boolean verifyOtp(String identifier, String otp) {
        OtpEntity entity = otpRepository
                .findTopByIdentifierAndIsUsedFalseOrderByCreatedAtDesc(identifier)
                .orElse(null);

        if (entity == null) return false;
        if (entity.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!entity.getOtp().equals(otp)) return false;

        entity.setUsed(true);
        otpRepository.save(entity);
        return true;
    }

    private void sendEmailOtp(String email, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("PsychoCare - Your OTP Code");
            message.setText(
                    "Your PsychoCare OTP is: " + otp +
                    "\n\nThis OTP is valid for " + OTP_EXPIRY_MINUTES + " minutes." +
                    "\n\nDo not share this OTP with anyone."
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage());
        }
    }

    // Clean up expired OTPs every hour
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanExpiredOtps() {
        // Simple cleanup - expired records are just left unused, verifyOtp checks expiry
        log.debug("OTP cleanup scheduled task ran");
    }
}
