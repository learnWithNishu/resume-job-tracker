package com.resume.job.tracker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "resume", indexes = {
        @Index(name = "idx_resume_user_id", columnList = "user_id"),
        @Index(name = "idx_resume_uploaded_at", columnList = "uploaded_at")
})
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String originalFileName;
    @Column(columnDefinition = "TEXT")
    private String parsedText;
    private LocalDateTime uploadedAt;
}
