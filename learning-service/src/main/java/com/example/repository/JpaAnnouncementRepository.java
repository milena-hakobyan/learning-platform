package com.example.repository;

import com.example.model.Announcement;
import com.example.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAnnouncementRepository extends JpaRepository<Announcement, Long> {
    Page<Announcement> findAllByCourseId(Long courseId, Pageable pageable);

    Page<Announcement> findAllByInstructorUserId(Long instructorUserId, Pageable pageable);
}
