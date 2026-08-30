package com.resume.job.tracker.service.Impl;

import com.resume.job.tracker.dto.ResumeUploadResponse;
import com.resume.job.tracker.entity.Resume;
import com.resume.job.tracker.entity.User;
import com.resume.job.tracker.exceptions.ResumeNotFoundException;
import com.resume.job.tracker.exceptions.UnauthorizedAccessException;
import com.resume.job.tracker.repository.ResumeRepository;
import com.resume.job.tracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void setUp(){
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

    @Test
    @DisplayName("getResumeById returns resume when user owns it")
    void getResumeById_ReturnResume_WhenUserOwnsIt(){
        when(resumeRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(testResume));
        ResumeUploadResponse response = resumeService.getResumeById(1L, "nk@test.com");

        assertNotNull(response);
        assertEquals("nk_resume.pdf", response.getOriginalFileName());
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("getResumeById throws UnauthorizedAccessException when wrong user")
    void getResumebyId_ThrowsException_WhenWrongUser(){
        when(resumeRepository.findByIdWithUser(1L)).thenReturn(Optional.of(testResume));
        assertThrows(
                UnauthorizedAccessException.class,
                ()-> resumeService.getResumeById(1L, "attacker@test.com")
        );

    }

    @Test
    @DisplayName("getResumeById throws ResumeNotFoundException when resume not found")
    void getResumeById_ThrowsException_WhenResumeNotFound(){
        when(resumeRepository.findByIdWithUser(999L)).thenReturn(Optional.empty());

        assertThrows(
                ResumeNotFoundException.class,
                ()-> resumeService.getResumeById(999L, "john@test.com")
        );

    }

    @Test
    @DisplayName("deleteResume successfully deletes when user owns resume")
    void deleteResume_Success_WhenUserOwnsResume(){
        when(resumeRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(testResume));
        resumeService.deleteResume(1L, "nk@test.com");

        verify(resumeRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteResume throws exception and never deletes when wrong user")
    void deleteResume_ThrowsException_WhenWrongUser(){
        when(resumeRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(testResume));
        assertThrows( UnauthorizedAccessException.class,
                ()-> resumeService.deleteResume(1L, "attacker@test.com"));
        verify(resumeRepository, never()).deleteById(any());
    }
    @Test
    @DisplayName("getAllResumes returns paginated list of resumes for user")
    void getAllResumes_ReturnsPaginatedList_ForValidUsers(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("uploadedAt").descending());
        Page<Resume> mockResumePage = new PageImpl<>(List.of(testResume), pageable, 1);

        when(userRepository.findByEmail("nk@test.com"))
                .thenReturn(Optional.of(testUser));
        when(resumeRepository.findByUserId(eq(testUser.getId()), any(Pageable.class)))
                .thenReturn(mockResumePage);

        Page<ResumeUploadResponse> result = resumeService.getAllResumes("nk@test.com", 0, 10);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("nk_resume.pdf", result.getContent().get(0).getOriginalFileName());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(userRepository, times(1)).findByEmail("nk@test.com");
        verify(resumeRepository, times(1)).findByUserId(eq(testUser.getId()), any(Pageable.class));
    }

    @Test
    @DisplayName("getResumeTestById returns full text when user owns resume")
    void getResumeTextById_ReturnsFullText_WhenUserOwnsIt(){
        when(resumeRepository.findByIdWithUser(1L))
                .thenReturn(Optional.of(testResume));
        String text = resumeService.getResumeTextById(1L, "nk@test.com");
        assertNotNull(text);
        assertTrue(text.contains("Java developer"));
        assertEquals(testResume.getParsedText(), text);
    }


    }
