package com.psychocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "therapist_awards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistAward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @Column(name = "award_name", nullable = false, length = 300)
    private String awardName;

    @Column(name = "awarding_body", length = 300)
    private String awardingBody;

    @Column(name = "year")
    private Integer year;
}
