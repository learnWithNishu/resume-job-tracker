package com.resume.job.tracker.repository;

import com.resume.job.tracker.entity.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.cdi.JpaRepositoryExtension;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    @Query("Select r from Resume r JOIN fetch r.user u where u.id= :userId")
    Page<Resume> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("Select r from Resume r JOIN Fetch r.user u where r.id = :id")
    Optional<Resume> findByIdWithUser(@Param("id") Long id);

}
