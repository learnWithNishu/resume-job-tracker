package com.resume.job.tracker.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeUploadResponse implements Serializable {

    private String originalFileName;
    private Long id;
    private LocalDateTime uploadedAt;
    private String parsedText;
    private static final long serialVersionUID = 1L;
}
