package com.resume.job.tracker.service;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResumeService {

    ResumeUploadResponse uploadResume(MultipartFile file, String email) throws IOException;

}
