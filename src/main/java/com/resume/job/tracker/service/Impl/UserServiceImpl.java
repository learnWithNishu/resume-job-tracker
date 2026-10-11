package com.resume.job.tracker.service.Impl;

import com.resume.job.tracker.client.TrackerServiceClient;
import com.resume.job.tracker.dto.*;
import com.resume.job.tracker.entity.Resume;
import com.resume.job.tracker.entity.User;
import com.resume.job.tracker.exceptions.EmailAlreadyExistsException;
import com.resume.job.tracker.exceptions.InvalidCredentialsException;
import com.resume.job.tracker.exceptions.UserNotFoundException;
import com.resume.job.tracker.repository.ResumeRepository;
import com.resume.job.tracker.repository.UserRepository;
import com.resume.job.tracker.service.JwtService;
import com.resume.job.tracker.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ResumeRepository resumeRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrackerServiceClient trackerServiceClient;
    private final JwtService jwtService;

    @Cacheable(value = "users", key = "#result.email")
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

    @Override
    public UserResponse getUser(Long id){
        User user = userRepository.findById(id).orElseThrow(()-> new UserNotFoundException("User not found with Id: " + id));
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    @Override
    public UserProfileResponse getUserProfile(String email) {
       User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(
               "User not found: " + email));
        List<Resume> resumes = resumeRepository.findByUserId(user.getId());
        int totalResumes = resumes.size();
        String mostRecentFileName = resumes.isEmpty() ? null :
                resumes.stream()
                        .max(Comparator.comparing(Resume::getUploadedAt))
                        .map(Resume::getOriginalFileName)
                        .orElse(null);
        TrackerStatsDto stats = trackerServiceClient.getApplicationStats(user.getId());

        List<RecentApplicationSummary> recentActivity = trackerServiceClient.getRecentApplications(user.getId());

        Double interviewRate = null;
        if(stats.getTotalApplications() != null && stats.getTotalApplications() > 0 && stats.getApplicationByStatus() != null){
            Long interviews = stats.getApplicationByStatus().getOrDefault("INTERVIEW_SCHEDULED", 0L);
            interviewRate = Math.round( (interviews * 100.0 / stats.getTotalApplications()) * 10.0) / 10.0;
        }

        String completeness;
        if(totalResumes == 0){
            completeness = "NEEDS_RESUME";
        } else if (stats.getTotalApplications() == 0) {
            completeness =  "NEEDS_APPLICATION";
        } else {
            completeness = "COMPLETE";
        }
        return  new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                totalResumes,
                mostRecentFileName,
                stats.getTotalApplications(),
                stats.getApplicationByStatus(),
                stats.getAverageMatchScore(),
                stats.getHighestMatchScore(),
                stats.getTotalWithMatchScore(),
                stats.getApplicationsThisMonth(),
                recentActivity,
                interviewRate,
                completeness
        );
    }


}
