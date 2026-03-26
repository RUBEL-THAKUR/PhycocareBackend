package com.psychocare.backend.service;

import com.psychocare.backend.dto.*;
import com.psychocare.backend.exception.BadRequestException;
import com.psychocare.backend.exception.ResourceNotFoundException;
import com.psychocare.backend.exception.UnauthorizedException;
import com.psychocare.backend.model.AppUser;
import com.psychocare.backend.model.Referral;
import com.psychocare.backend.model.TransactionType;
import com.psychocare.backend.model.WalletTransaction;
import com.psychocare.backend.repository.AppUserRepository;
import com.psychocare.backend.repository.ReferralRepository;
import com.psychocare.backend.repository.WalletTransactionRepository;
import com.psychocare.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final double REFERRAL_CREDIT_AMOUNT = 200.0;

    private final AppUserRepository userRepository;
    private final ReferralRepository referralRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    @Transactional
    public UserLoginResponse signup(UserSignupRequest request) {
        if (userRepository.existsByEmailId(request.getEmailId())) {
            throw new BadRequestException("Email address is already registered");
        }
        if (request.getMobileNumber() != null && userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number is already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        AppUser user = AppUser.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .emailId(request.getEmailId())
                .mobileNumber(request.getMobileNumber())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .walletBalance(0.0)
                .referralCode(generateReferralCode())
                .active(true)
                .deleted(false)
                .build();

        // Apply referral bonus if a valid code was provided
        if (request.getReferralCode() != null && !request.getReferralCode().isBlank()) {
            user.setReferredBy(request.getReferralCode());
        }

        AppUser saved = userRepository.save(user);

        // Process referral credit after user is persisted
        if (request.getReferralCode() != null && !request.getReferralCode().isBlank()) {
            processReferralOnSignup(saved, request.getReferralCode());
        }

        String token = jwtUtil.generateUserToken(saved.getUserId(), saved.getEmailId());
        return buildLoginResponse(token, saved);
    }

    @Transactional
    public UserLoginResponse loginWithPassword(UserLoginRequest request) {
        String identifier = request.getEmailId() != null ? request.getEmailId() : request.getMobileNumber();
        if (identifier == null || identifier.isBlank()) {
            throw new BadRequestException("Email or mobile number is required");
        }

        AppUser user = findByIdentifier(identifier);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BadRequestException("This account uses OTP login. Please use OTP to sign in.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException("Account is deactivated. Contact support.");
        }

        String token = jwtUtil.generateUserToken(user.getUserId(), user.getEmailId());
        return buildLoginResponse(token, user);
    }

    @Transactional
    public UserLoginResponse loginWithOtp(String identifier, String otp) {
        if (!otpService.verifyOtp(identifier, otp)) {
            throw new UnauthorizedException("Invalid or expired OTP");
        }

        AppUser user = findByIdentifier(identifier);

        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new UnauthorizedException("Account is deactivated. Contact support.");
        }

        String token = jwtUtil.generateUserToken(user.getUserId(), user.getEmailId());
        return buildLoginResponse(token, user);
    }

    public AppUser getByEmail(String email) {
        return userRepository.findByEmailId(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AppUser getById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public AppUser updateProfile(Long userId, UserProfileUpdateRequest request) {
        AppUser user = getById(userId);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getTimezone() != null) user.setTimezone(request.getTimezone());
        if (request.getMobileNumber() != null) {
            if (!request.getMobileNumber().equals(user.getMobileNumber())
                    && userRepository.existsByMobileNumber(request.getMobileNumber())) {
                throw new BadRequestException("Mobile number is already in use");
            }
            user.setMobileNumber(request.getMobileNumber());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        AppUser user = getById(userId);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public WalletResponse addMoney(Long userId, AddMoneyRequest request) {
        AppUser user = getById(userId);
        double newBalance = user.getWalletBalance() + request.getAmount();
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .type(TransactionType.CREDIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description("Wallet top-up via " + (request.getPaymentMethod() != null ? request.getPaymentMethod() : "online payment"))
                .referenceId(request.getReferenceId())
                .build();
        walletTransactionRepository.save(tx);

        return WalletResponse.builder()
                .userId(userId)
                .balance(newBalance)
                .build();
    }

    public WalletResponse getWallet(Long userId) {
        AppUser user = getById(userId);
        return WalletResponse.builder()
                .userId(userId)
                .balance(user.getWalletBalance())
                .build();
    }

    // Internal method called by session booking to deduct wallet balance
    @Transactional
    public void deductWallet(AppUser user, double amount, String description) {
        if (user.getWalletBalance() < amount) {
            throw new BadRequestException("Insufficient wallet balance. Please add money to your wallet.");
        }
        double newBalance = user.getWalletBalance() - amount;
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .type(TransactionType.DEBIT)
                .amount(amount)
                .balanceAfter(newBalance)
                .description(description)
                .build();
        walletTransactionRepository.save(tx);
    }

    // Internal method for refunding cancelled sessions
    @Transactional
    public void refundWallet(AppUser user, double amount, String description) {
        double newBalance = user.getWalletBalance() + amount;
        user.setWalletBalance(newBalance);
        userRepository.save(user);

        WalletTransaction tx = WalletTransaction.builder()
                .user(user)
                .type(TransactionType.REFUND)
                .amount(amount)
                .balanceAfter(newBalance)
                .description(description)
                .build();
        walletTransactionRepository.save(tx);
    }

    private void processReferralOnSignup(AppUser newUser, String referralCode) {
        AppUser referrer = userRepository.findByReferralCode(referralCode).orElse(null);
        if (referrer == null) {
            log.warn("Referral code {} not found during signup for user {}", referralCode, newUser.getEmailId());
            return;
        }
        if (referralRepository.existsByReferralCodeAndReferredUserUserId(referralCode, newUser.getUserId())) {
            return;
        }

        Referral referral = Referral.builder()
                .referrer(referrer)
                .referredUser(newUser)
                .referralCode(referralCode)
                .sessionBooked(false)
                .creditIssued(false)
                .creditAmount(REFERRAL_CREDIT_AMOUNT)
                .build();
        referralRepository.save(referral);
    }

    // Called by SessionService when a referred user books their first session
    @Transactional
    public void processReferralCreditOnFirstSession(AppUser user) {
        if (user.getReferredBy() == null) return;

        AppUser referrer = userRepository.findByReferralCode(user.getReferredBy()).orElse(null);
        if (referrer == null) return;

        referralRepository.findByReferrerUserId(referrer.getUserId(), org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(r -> r.getReferredUser().getUserId().equals(user.getUserId()))
                .filter(r -> !r.getCreditIssued())
                .findFirst()
                .ifPresent(referral -> {
                    referral.setSessionBooked(true);
                    referral.setCreditIssued(true);
                    referralRepository.save(referral);

                    // Credit referrer wallet
                    double newBalance = referrer.getWalletBalance() + REFERRAL_CREDIT_AMOUNT;
                    referrer.setWalletBalance(newBalance);
                    userRepository.save(referrer);

                    WalletTransaction tx = WalletTransaction.builder()
                            .user(referrer)
                            .type(TransactionType.REFERRAL_BONUS)
                            .amount(REFERRAL_CREDIT_AMOUNT)
                            .balanceAfter(newBalance)
                            .description("Referral bonus - " + user.getFirstName() + " booked their first session")
                            .build();
                    walletTransactionRepository.save(tx);

                    log.info("Referral credit of {} issued to user {}", REFERRAL_CREDIT_AMOUNT, referrer.getEmailId());
                });
    }

    private AppUser findByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmailId(identifier)
                    .orElseThrow(() -> new UnauthorizedException("No account found with this email address"));
        }
        return userRepository.findByMobileNumber(identifier)
                .orElseThrow(() -> new UnauthorizedException("No account found with this mobile number"));
    }

    private String generateReferralCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
        } while (userRepository.findByReferralCode(code).isPresent());
        return code;
    }

    private UserLoginResponse buildLoginResponse(String token, AppUser user) {
        return UserLoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .username(user.getUsername())
                .walletBalance(user.getWalletBalance())
                .referralCode(user.getReferralCode())
                .profileImage(user.getProfileImage())
                .build();
    }
}
