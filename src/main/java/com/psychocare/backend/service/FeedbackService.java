package com.psychocare.backend.service;

import com.psychocare.backend.dto.FeedbackRequest;
import com.psychocare.backend.exception.ResourceNotFoundException;
import com.psychocare.backend.model.AppUser;
import com.psychocare.backend.model.Feedback;
import com.psychocare.backend.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    @Transactional
    public Feedback submit(Long userId, FeedbackRequest request) {
        AppUser user = userService.getById(userId);
        Feedback feedback = Feedback.builder()
                .user(user)
                .qualityRating(request.getQualityRating())
                .helpfulnessRating(request.getHelpfulnessRating())
                .clarityRating(request.getClarityRating())
                .source(request.getSource())
                .comment(request.getComment())
                .isAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false)
                .build();
        return feedbackRepository.save(feedback);
    }

    public Page<Feedback> getUserFeedback(Long userId, Pageable pageable) {
        return feedbackRepository.findByUserUserId(userId, pageable);
    }
}
