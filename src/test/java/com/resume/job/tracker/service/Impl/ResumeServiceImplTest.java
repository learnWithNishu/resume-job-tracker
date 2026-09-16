package com.resume.job.tracker.service.Impl;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.dto.SaveGeneratedResumeRequest;
import com.resume.job.tracker.entity.Resume;
import com.resume.job.tracker.entity.User;
import com.resume.job.tracker.exceptions.ResumeNotFoundException;
import com.resume.job.tracker.exceptions.UnauthorizedAccessException;
import com.resume.job.tracker.exceptions.UserNotFoundException;
import com.resume.job.tracker.repository.ResumeRepository;
import com.resume.job.tracker.repository.UserRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResumeServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResumeRepository resumeRepository;

    @InjectMocks
    private ResumeServiceImpl resumeService;

    private User testUser;
    private Resume testResume;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("nk@test.com");
        testUser.setName("nk");

        testResume = new Resume();
        testResume.setId(1L);
        testResume.setUser(testUser);
        testResume.setOriginalFileName("nk_resume.pdf");
        testResume.setParsedText("Java developer with 2 years experience in Spring Boot and microservices");
        testResume.setUploadedAt(LocalDateTime.now());
    }

    private byte[] createDummyPdfBytes() throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Java developer with 2 years experience");
                contentStream.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            document.save(out);
            return out.toByteArray();
        }
    }

    @Test
    @DisplayName("uploadResume successfully extracts text and saves record")
    void uploadResume_Success() throws IOException {
        byte[] pdfBytes = createDummyPdfBytes();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        when(userRepository.findByEmail("nk@test.com")).thenReturn(Optional.of(testUser));
        when(resumeRepository.save(any(Resume.class))).thenAnswer(invocation -> {
            Resume r = invocation.getArgument(0);
            r.setId(10L);
            return r;
        });

        ResumeUploadResponse response = resumeService.uploadResume(file, "nk@test.com");

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("resume.pdf", response.getOriginalFileName());
        assertTrue(response.getParsedText().contains("Java developer"));
        verify(resumeRepository, times(1)).save(any(Resume.class));
    }

    @Test
    @DisplayName("uploadResume throws UserNotFoundException when user email does not exist")
    void uploadResume_ThrowsException_WhenUserNotFound() throws IOException {
        byte[] pdfBytes = createDummyPdfBytes();
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf", pdfBytes);

        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> resumeService.uploadResume(file, "missing@test.com"));
        verify(resumeRepository, never()).save(any(Resume.class));
    }

    @Test
    @DisplayName("getResumeById returns resume when user owns it")
    void getResumeById_ReturnResume_WhenUserOwnsIt() {
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));

        ResumeUploadResponse response = resumeService.getResumeById(1L, "nk@test.com");

        assertNotNull(response);
        assertEquals("nk_resume.pdf", response.getOriginalFileName());
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("getResumeById throws UnauthorizedAccessException when wrong user")
    void getResumeById_ThrowsException_WhenWrongUser() {
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));

        assertThrows(
                UnauthorizedAccessException.class,
                () -> resumeService.getResumeById(1L, "attacker@test.com")
        );
    }

    @Test
    @DisplayName("getResumeById throws ResumeNotFoundException when resume not found")
    void getResumeById_ThrowsException_WhenResumeNotFound() {
        when(resumeRepository.findByIdWithUser(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResumeNotFoundException.class,
                () -> resumeService.getResumeById(999L, "john@test.com")
        );
    }

    @Test
    @DisplayName("deleteResume successfully deletes when user owns resume")
    void deleteResume_Success_WhenUserOwnsResume() {
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));

        resumeService.deleteResume(1L, "nk@test.com");

        verify(resumeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteResume throws exception and never deletes when wrong user")
    void deleteResume_ThrowsException_WhenWrongUser() {
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));

        assertThrows(
                UnauthorizedAccessException.class,
                () -> resumeService.deleteResume(1L, "attacker@test.com")
        );
        verify(resumeRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getAllResumes returns paginated list of resumes for user")
    void getAllResumes_ReturnsPaginatedList_ForValidUsers() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("uploadedAt").descending());
        Page mockResumePage = new PageImpl<>(List.of(testResume), pageable, 1);

        when(userRepository.findByEmail("nk@test.com")).thenReturn(Optional.of(testUser));
        when(resumeRepository.findByUserId(eq(testUser.getId()), any(Pageable.class)))
                .thenReturn(mockResumePage);

        Page result = resumeService.getAllResumes("nk@test.com", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        verify(userRepository, times(1)).findByEmail("nk@test.com");
        verify(resumeRepository, times(1)).findByUserId(eq(testUser.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("getResumeTextById returns full text when user owns resume")
    void getResumeTextById_ReturnsFullText_WhenUserOwnsIt() {
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));

        String text = resumeService.getResumeTextById(1L, "nk@test.com");

        assertNotNull(text);
        assertTrue(text.contains("Java developer"));
        assertEquals(testResume.getParsedText(), text);
    }

    @Test
    @DisplayName("saveGeneratedResume constructs filename and persists tailored resume")
    void saveGeneratedResume_Success() {
        SaveGeneratedResumeRequest request = new SaveGeneratedResumeRequest();
        request.setJobTitle("Software Engineer");
        request.setCompanyName("Google India");
        request.setGeneratedResumeText("Tailored resume details for Google");

        when(userRepository.findByEmail("nk@test.com")).thenReturn(Optional.of(testUser));
        when(resumeRepository.save(any(Resume.class))).thenAnswer(invocation -> {
            Resume r = invocation.getArgument(0);
            r.setId(50L);
            return r;
        });

        ResumeUploadResponse response = resumeService.saveGeneratedResume(request, "nk@test.com");

        assertNotNull(response);
        assertEquals(50L, response.getId());
        assertEquals("tailored_Software_Engineer_Google_India.txt", response.getOriginalFileName());
        assertEquals("Tailored resume details for Google", response.getParsedText());
        verify(resumeRepository, times(1)).save(any(Resume.class));
    }

    @Test
    @DisplayName("saveGeneratedResume throws UserNotFoundException when user email not registered")
    void saveGeneratedResume_ThrowsException_WhenUserNotFound() {
        SaveGeneratedResumeRequest request = new SaveGeneratedResumeRequest();
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> resumeService.saveGeneratedResume(request, "missing@test.com"));
        verify(resumeRepository, never()).save(any(Resume.class));
    }

    @Test
    @DisplayName("deleteResumeWithTextEviction deletes resume when owned by user")
    void deleteResumeWithTextEviction_Success() {
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));

        resumeService.deleteResumeWithTextEviction(1L, "nk@test.com");

        verify(resumeRepository, times(1)).deleteById(1L);
    }
}