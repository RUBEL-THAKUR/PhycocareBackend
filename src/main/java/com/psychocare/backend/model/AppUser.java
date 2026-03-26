package com.psychocare.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email_id", unique = true, length = 255)
    private String emailId;

    @Column(name = "mobile_number", unique = true, length = 15)
    private String mobileNumber;

    @Column(name = "username", unique = true, length = 100)
    private String username;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "timezone", length = 100)
    private String timezone;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(name = "referral_code", unique = true, length = 20)
    private String referralCode;

    @Column(name = "referred_by", length = 20)
    private String referredBy;

    @Column(name = "wallet_balance")
    @Builder.Default
    private Double walletBalance = 0.0;

    /*
     * Named 'active' and 'deleted' (not 'isActive'/'isDeleted') to prevent
     * Lombok from generating duplicate isActive()/isDeleted() accessor names
     * that conflict with the standard getter pattern.
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
