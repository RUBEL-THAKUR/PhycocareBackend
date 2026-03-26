package com.psychocare.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "referrals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Referral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_user_id", nullable = false)
    private AppUser referrer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_user_id", nullable = false)
    private AppUser referredUser;

    @Column(name = "referral_code", nullable = false, length = 20)
    private String referralCode;

    @Column(name = "session_booked")
    @Builder.Default
    private Boolean sessionBooked = false;

    @Column(name = "credit_issued")
    @Builder.Default
    private Boolean creditIssued = false;

    @Column(name = "credit_amount")
    private Double creditAmount;

    @CreationTimestamp
    @Column(name = "used_on", updatable = false)
    private LocalDateTime usedOn;
}
