package com.resume.job.tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.resume.job.tracker.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
 
    List<User> findByEmail(String email);
}
