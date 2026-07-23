package com.resume.job.tracker.service.Impl;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.entity.Resume;
import com.resume.job.tracker.entity.User;
import com.resume.job.tracker.exceptions.EmailAlreadyExistsException;
import com.resume.job.tracker.repository.ResumeRepository;
import com.resume.job.tracker.repository.UserRepository;
import com.resume.job.tracker.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeServiceImpl implements ResumeService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;

    @Override
    public ResumeUploadResponse uploadResume(MultipartFile file, String email) throws IOException {

        String newText;
        try(PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper pdfTextStripper = new PDFTextStripper();
             newText= pdfTextStripper.getText(document);
        }
        User user = userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not exists: " + email));
        Resume resume = new Resume();
        resume.setUser(user);
        resume.setUploadedAt(LocalDateTime.now());
        resume.setOriginalFileName(file.getOriginalFilename());
        resume.setParsedText(newText);
        Resume uploadedResume = resumeRepository.save(resume);
        ResumeUploadResponse response = new ResumeUploadResponse();
        response.setId(uploadedResume.getId());
        response.setOriginalFileName(uploadedResume.getOriginalFileName());
        response.setUploadedAt(uploadedResume.getUploadedAt());
        response.setParsedText(uploadedResume.getParsedText().substring(0, Math.min(200, uploadedResume.getParsedText().length())));

        return response;
    }
}
