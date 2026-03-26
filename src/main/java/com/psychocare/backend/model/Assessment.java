package com.psychocare.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Column(name = "test_slug", nullable = false, length = 100)
    private String testSlug;

    @Column(name = "score")
    private Integer score;

    @Column(name = "result_label", length = 200)
    private String resultLabel;

    @Column(name = "answers", columnDefinition = "TEXT")
    private String answers;

    @Column(name = "completed")
    @Builder.Default
    private Boolean completed = false;

    @CreationTimestamp
    @Column(name = "taken_at", updatable = false)
    private LocalDateTime takenAt;
}
