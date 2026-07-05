package com.resume.job.tracker.service;

import java.util.List;

import com.resume.job.tracker.dto.UserRegisterRequest;
import com.resume.job.tracker.dto.UserResponse;
import com.resume.job.tracker.entity.User;

public interface UserService {

    UserResponse registerUser(UserRegisterRequest request);
}
