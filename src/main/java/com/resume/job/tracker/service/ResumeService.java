package com.resume.job.tracker.service;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.dto.SaveGeneratedResumeRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResumeService {

    ResumeUploadResponse uploadResume(MultipartFile file, String email) throws IOException;
    Page<ResumeUploadResponse> getAllResumes(String email, int page, int size);
    ResumeUploadResponse getResumeById(Long id, String email);
    void deleteResume(Long id, String email);
    String getResumeTextById(Long resumeId, String email);
    ResumeUploadResponse saveGeneratedResume(SaveGeneratedResumeRequest request, String email);
}
