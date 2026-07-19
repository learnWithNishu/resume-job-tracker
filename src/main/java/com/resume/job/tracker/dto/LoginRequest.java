package com.resume.job.tracker.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
public class LoginRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
}
