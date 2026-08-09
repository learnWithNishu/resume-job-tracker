package com.resume.job.tracker.controller;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.dto.SaveGeneratedResumeRequest;
import com.resume.job.tracker.exceptions.UnauthorizedAccessException;
import com.resume.job.tracker.service.ResumeService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> getResume(@PathVariable Long id)  {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.status(HttpStatus.OK).body(resumeService.getResumeById(id, email));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        resumeService.deleteResume(id, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/text")
    public ResponseEntity<String> getResumeText(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(resumeService.getResumeTextById(id, email));
    }

    @PostMapping("/save-generated")
    public ResponseEntity<ResumeUploadResponse> saveGeneratedResume(@RequestBody @Valid SaveGeneratedResumeRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
      ResumeUploadResponse response = resumeService.saveGeneratedResume(request, email);
        return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
