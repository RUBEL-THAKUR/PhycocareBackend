package com.psychocare.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "therapist_experiences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TherapistExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    @Column(name = "organization", nullable = false, length = 300)
    private String organization;

    @Column(name = "designation", nullable = false, length = 200)
    private String designation;

    @Column(name = "from_year", nullable = false)
    private Integer fromYear;

    @Column(name = "to_year")
    private Integer toYear;

    @Column(name = "is_current")
    private boolean current = false;
}
