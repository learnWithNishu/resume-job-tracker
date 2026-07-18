package com.resume.job.tracker.controller;


import com.resume.job.tracker.dto.UserRegisterRequest;
import com.resume.job.tracker.dto.UserResponse;
import com.resume.job.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<UserResponse> createNewUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(userRegisterRequest));
    }

}
