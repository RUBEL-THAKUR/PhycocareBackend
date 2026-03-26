package com.psychocare.backend.dto;

import com.psychocare.backend.model.TherapistStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TherapistPublicResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String category;
    private String briefDescription;
    private Integer experience;
    private Double rating;
    private Integer totalSessions;
    private String profileImageUrl;
    private Double consultationFeeChat;
    private Double consultationFeeAudio;
    private Double consultationFeeVideo;
    private List<String> specializations;
    private List<String> areasOfExpertise;
    private TherapistStatus status;
}
