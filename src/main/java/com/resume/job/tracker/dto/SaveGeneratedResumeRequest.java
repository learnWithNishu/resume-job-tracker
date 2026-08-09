package com.resume.job.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveGeneratedResumeRequest {
    private String generatedResumeText;
    private String jobTitle;
    private String companyName;
}
