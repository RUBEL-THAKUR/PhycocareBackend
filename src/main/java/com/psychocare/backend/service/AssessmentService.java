package com.psychocare.backend.service;

import com.psychocare.backend.dto.AssessmentSubmitRequest;
import com.psychocare.backend.model.AppUser;
import com.psychocare.backend.model.Assessment;
import com.psychocare.backend.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final UserService userService;

    @Transactional
    public Assessment submit(Long userId, AssessmentSubmitRequest request) {
        AppUser user = userService.getById(userId);
        Assessment assessment = Assessment.builder()
                .user(user)
                .testName(request.getTestName())
                .testSlug(request.getTestSlug())
                .score(request.getScore())
                .resultLabel(request.getResultLabel())
                .answers(request.getAnswers())
                .completed(true)
                .build();
        return assessmentRepository.save(assessment);
    }

    public List<Assessment> getCompleted(Long userId) {
        return assessmentRepository.findByUserUserIdAndCompleted(userId, true);
    }

    public List<Assessment> getAll(Long userId) {
        return assessmentRepository.findByUserUserIdAndCompleted(userId, false);
    }
}
