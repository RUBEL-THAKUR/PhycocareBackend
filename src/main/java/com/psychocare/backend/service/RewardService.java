package com.psychocare.backend.service;

import com.psychocare.backend.model.Reward;
import com.psychocare.backend.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository rewardRepository;

    public List<Reward> getActiveRewards() {
        return rewardRepository.findByIsActiveTrue();
    }
}
