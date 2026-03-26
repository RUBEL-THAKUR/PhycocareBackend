package com.psychocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "therapist_professional_memberships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistProfessionalMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @Column(name = "organization_name", nullable = false, length = 300)
    private String organizationName;

    @Column(name = "membership_id", length = 100)
    private String membershipId;

    @Column(name = "valid_till", length = 20)
    private String validTill;
}
