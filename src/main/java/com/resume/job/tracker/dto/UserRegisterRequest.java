package com.resume.job.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRegisterRequest {

    private String name;
    @NotBlank
    @Email  
    private String email;
    private String password;
}
