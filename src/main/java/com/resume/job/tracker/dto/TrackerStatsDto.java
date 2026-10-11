package com.resume.job.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrackerStatsDto {

    private Long totalApplications;
    private Map<String, Long> applicationByStatus;
    private Double averageMatchScore;
    private Long totalWithMatchScore;
    private Double highestMatchScore;
    private Long applicationsThisMonth;
}
