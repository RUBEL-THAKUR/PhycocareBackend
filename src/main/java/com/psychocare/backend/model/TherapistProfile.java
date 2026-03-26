package com.psychocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "therapist_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", referencedColumnName = "id", nullable = false, unique = true)
    private Therapist therapist;

    @Column(length = 20)
    private String prefix;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String middleName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 100)
    private String category;

    @Column(length = 255)
    private String emailId;

    @Column(length = 100)
    private String userName;

    @Column(length = 15)
    private String mobile;

    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(length = 100)
    private String language;

    @Column(length = 2000)
    private String briefDescription;

    @Column(length = 500)
    private String presentAddress;

    @Column(length = 100)
    private String presentCountry;

    @Column(length = 100)
    private String presentState;

    @Column(length = 100)
    private String presentCity;

    @Column(length = 100)
    private String presentDistrict;

    @Column(length = 20)
    private String presentPinCode;

    @Column(length = 500)
    private String clinicAddress;

    @Column(length = 100)
    private String clinicCountry;

    @Column(length = 100)
    private String clinicState;

    @Column(length = 100)
    private String clinicCity;

    @Column(length = 100)
    private String clinicDistrict;

    @Column(length = 20)
    private String clinicPinCode;

    @Column(length = 100)
    private String timeZone;

    private Integer experience;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "consultation_fee_chat")
    private Double consultationFeeChat;

    @Column(name = "consultation_fee_audio")
    private Double consultationFeeAudio;

    @Column(name = "consultation_fee_video")
    private Double consultationFeeVideo;
}
