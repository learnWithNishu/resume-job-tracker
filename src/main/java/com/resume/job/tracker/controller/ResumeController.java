package com.resume.job.tracker.controller;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.service.ResumeService;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;
    @PostMapping("/upload")
    public ResponseEntity<ResumeUploadResponse> resumeUploaded(@RequestParam("file")MultipartFile file) throws IOException {
       String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.CREATED).body( resumeService.uploadResume(file,
                email));
    }

    @GetMapping
    public ResponseEntity<?> getAllResumes(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.OK).body(resumeService.getAllResumes(email));
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getResume(@PathVariable Long id) throws IllegalAccessException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.OK).body(resumeService.getResumeById(id, email));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id) throws IllegalAccessException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        resumeService.deleteResume(id, email);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
