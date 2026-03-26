package com.psychocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "therapist_qualifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistQualification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @Column(name = "degree", nullable = false, length = 200)
    private String degree;

    @Column(name = "institution", nullable = false, length = 300)
    private String institution;

    @Column(name = "passing_year", nullable = false)
    private Integer passingYear;

    @Column(name = "specialization", length = 200)
    private String specialization;
}
