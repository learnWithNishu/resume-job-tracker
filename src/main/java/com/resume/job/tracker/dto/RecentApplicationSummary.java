package com.resume.job.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecentApplicationSummary {

    private Long id;
    private String companyName;
    private String jobTitle;
    private String status;
    private LocalDateTime appliedAt;
    private Double matchScore;
}