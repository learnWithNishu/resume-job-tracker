package com.resume.job.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse implements Serializable {
    
    private Long id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private String password;
    private static final long serialVersionUID = 1L;

}
