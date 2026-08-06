package com.resume.job.tracker.service.Impl;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.entity.Resume;
import com.resume.job.tracker.entity.User;
import com.resume.job.tracker.exceptions.ResumeNotFoundException;
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
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<ResumeUploadResponse> getAllResumes(String email){
       User user = userRepository.findByEmail(email).orElseThrow();
       List<Resume> resumes = resumeRepository.findByUserId(user.getId());
       List<ResumeUploadResponse> resumeUploadResponses = new ArrayList<>();
       ResumeUploadResponse response = new ResumeUploadResponse();
       for(Resume resume: resumes){
           response.setParsedText(resume.getParsedText().substring(0, Math.min(200, resume.getParsedText().length())));
           response.setUploadedAt(resume.getUploadedAt());
           response.setId(resume.getId());
           response.setOriginalFileName(resume.getOriginalFileName());
       }
       resumeUploadResponses.add(response);
        return resumeUploadResponses;
    }

    @Override
    public ResumeUploadResponse getResumeById(Long id, String email) throws IllegalAccessException {
        Resume rs = resumeRepository.findById(id).orElseThrow(()-> new ResumeNotFoundException("Resume not found!"));
        if(!rs.getUser().getEmail().equals(email)){
            throw new IllegalAccessException("You do not have permission to access this resume.");
        }
        ResumeUploadResponse rsUploadResponse = new ResumeUploadResponse();
        rsUploadResponse.setOriginalFileName(rs.getOriginalFileName());
        rsUploadResponse.setUploadedAt(rs.getUploadedAt());
        rsUploadResponse.setId(rs.getId());
        rsUploadResponse.setParsedText(rs.getParsedText());
        return  rsUploadResponse;
    }
    @Override
    public void deleteResume(Long id, String email) throws IllegalAccessException {
        Resume dlResume = resumeRepository.findById(id).orElseThrow(()-> new ResumeNotFoundException("Resume not found!"));
        if(!dlResume.getUser().getEmail().equals(email)){
            throw new IllegalAccessException("You do not have permission to delete this resume.");
        }else{
            resumeRepository.deleteById(id);
        }

    }

    @Override
    public String getResumeTextById(Long resumeId, Long userId) throws IllegalAccessException {
        Resume resume = resumeRepository.findById(resumeId).orElseThrow(()-> new ResumeNotFoundException("Resume not found by Id: !" + resumeId));
        if(!resume.getUser().getId().equals(userId)){
         throw new IllegalAccessException("You do not have permission to delete this resume.");

        }
        return resume.getParsedText();
    }

}
