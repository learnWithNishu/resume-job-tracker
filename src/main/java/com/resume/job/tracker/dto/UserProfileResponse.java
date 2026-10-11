package com.resume.job.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String name;
    private String email;
    private LocalDateTime joinedAt;

    private int totalResumes;
    private String mostRecentResumeFileName;

    private Long totalApplications;
    private Map<String, Long> applicationsByStatus;

    private Double averageMatchScore;
    private Double highestMatchScore;
    private Long totalWithMatchScore;
    private Long applicationsThisMonth;

    // recent activity — last 5 applications
    private List<RecentApplicationSummary> recentActivity;

    // computed fields — calculated from above data
    private Double interviewRate;

    private String profileCompleteness;
}