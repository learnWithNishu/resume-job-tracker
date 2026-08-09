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
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    @Override
    public UserResponse registerUser(UserRegisterRequest request) {
       if(userRepository.findByEmail(request.getEmail()).isPresent()){
           throw new EmailAlreadyExistsException("Email already registered: " + request.getEmail());
       }

       User user = new User();
       user.setName(request.getName());
       user.setEmail(request.getEmail());
       user.setPassword(passwordEncoder.encode(request.getPassword()));
       user.setCreatedAt(LocalDateTime.now());

       User savedUser = userRepository.save(user);

       UserResponse response = new UserResponse();
       response.setId(savedUser.getId());
       response.setName(savedUser.getName());
       response.setEmail(savedUser.getEmail());
       response.setCreatedAt(savedUser.getCreatedAt());
        return response;
    }

    public LoginResponse loginUser(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new UserNotFoundException("User not found with emailId: " + loginRequest.getEmail()));

        String rawPassword = loginRequest.getPassword();
        String storedHashPassword = user.getPassword();
        boolean matchPassword = passwordEncoder.matches(rawPassword, storedHashPassword);
        if(!matchPassword){
            throw new InvalidCredentialsException("Wrong password");
        }
        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new LoginResponse(token);
    }


}
