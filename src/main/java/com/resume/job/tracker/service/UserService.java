package com.resume.job.tracker.service;

import java.util.List;

import com.resume.job.tracker.dto.UserRegisterRequest;
import com.resume.job.tracker.entity.User;

public interface UserService {

    List<User> registerUser(UserRegisterRequest request);
}
