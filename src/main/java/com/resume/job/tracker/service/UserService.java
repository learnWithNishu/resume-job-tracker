package com.resume.job.tracker.service;


import com.resume.job.tracker.dto.*;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);
    LoginResponse loginUser(LoginRequest loginRequest);
    UserResponse getUser(Long id);
    UserProfileResponse getUserProfile(String email);
}
