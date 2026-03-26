package com.psychocare.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(name = "quality_rating", nullable = false)
    private Integer qualityRating;

    @Column(name = "helpfulness_rating", nullable = false)
    private Integer helpfulnessRating;

    @Column(name = "clarity_rating", nullable = false)
    private Integer clarityRating;

    @Column(name = "source", length = 200)
    private String source;

    @Column(name = "comment", length = 2000)
    private String comment;

    @Column(name = "is_anonymous")
    @Builder.Default
    private Boolean isAnonymous = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
