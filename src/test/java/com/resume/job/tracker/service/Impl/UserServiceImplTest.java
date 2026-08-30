package com.resume.job.tracker.service.Impl;

import com.resume.job.tracker.dto.LoginRequest;
import com.resume.job.tracker.dto.LoginResponse;
import com.resume.job.tracker.dto.UserRegisterRequest;
import com.resume.job.tracker.dto.UserResponse;
import com.resume.job.tracker.entity.User;
import com.resume.job.tracker.exceptions.EmailAlreadyExistsException;
import com.resume.job.tracker.exceptions.InvalidCredentialsException;
import com.resume.job.tracker.exceptions.UserNotFoundException;
import com.resume.job.tracker.repository.UserRepository;
import com.resume.job.tracker.service.JwtService;
import com.resume.job.tracker.service.UserService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private UserServiceImpl userService;
    private UserRegisterRequest validRegisterRequest;
    private User savedUser;

    @BeforeEach
    void setUp(){
        validRegisterRequest = new UserRegisterRequest();
        validRegisterRequest.setEmail("nk@test.com");
        validRegisterRequest.setName("nk");
        validRegisterRequest.setPassword("nk@123");

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("nk");
        savedUser.setEmail("nk@test.com");
        savedUser.setPassword("hashedPassword");
        savedUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Register user successfully when email is not taken")
    void registerUser_Success_WhenEmailNotExists() {
        // ARRANGE
        when(userRepository.findByEmail("nk@test.com"))
                .thenReturn(Optional.empty()); // email not in DB
        when(passwordEncoder.encode("nk@123"))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // ACT
        UserResponse response = userService.registerUser(validRegisterRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals("nk@test.com", response.getEmail());
        assertEquals("nk", response.getName());
        assertNull(response.getPassword()); // password must never be in response

        // verify save was called exactly once with any User object
        verify(userRepository, times(1)).save(any(User.class));
        // verify password was encoded — never stored plain
        verify(passwordEncoder, times(1)).encode("nk@123");
    }

    @Test
    @DisplayName("Register throws EmailAlreadyExistsException when email taken")
    void registerUser_ThrowsException_WhenEmailAlreadyExists(){
        when(userRepository.findByEmail("nk@test.com"))
                .thenReturn(Optional.of(savedUser));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                ()-> userService.registerUser(validRegisterRequest)
        );
        assertTrue(exception.getMessage().contains("nk@test.com"));

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Login returns token when credentails are correct")
    void loginUser_ReturnsToken_WhenCredentialsCorrect(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nk@test.com");
        loginRequest.setPassword("nk@123");
        when(userRepository.findByEmail("nk@test.com"))
                .thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("nk@123", "hashedPassword"))
                .thenReturn(true);
        when(jwtService.generateToken("nk@test.com", 1L))
                .thenReturn("mock.jwt.token");
        LoginResponse response = userService.loginUser(loginRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        verify(jwtService, times(1)).generateToken("nk@test.com", 1L);

    }

    @Test
    @DisplayName("Login throws UserNotFoundException when email not registered")
    void loginUser_ThrowsException_WhenEmailNotFound(){
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@test.com");
        request.setPassword("password@123");
        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                ()-> userService.loginUser(request));
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Login throws InvalidCredentailsException when password wrong")
    void loginUser_ThrowsException_WhenPasswordWrong(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nk@test.com");
        loginRequest.setPassword("wrongPassword");
        when(userRepository.findByEmail("nk@test.com"))
                .thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword"))
                .thenReturn(false);
        assertThrows(
                InvalidCredentialsException.class,
                ()-> userService.loginUser(loginRequest)
        );
        verify(jwtService, never()).generateToken(anyString(), anyLong());
    }


}
