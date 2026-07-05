package com.resume.job.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank
    private String name;
    @NotBlank
    @Email  
    private String email;
    @NotBlank
    private String password;
}
