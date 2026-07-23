package com.resume.job.tracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeUploadResponse {

    private String originalFileName;
    private Long id;
    private LocalDateTime uploadedAt;
    private String parsedText;
}
