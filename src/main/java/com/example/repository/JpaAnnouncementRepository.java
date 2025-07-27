package com.example.repository;

import com.example.model.ActivityLog;
import com.example.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaAnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByCourseId(Long courseId);

    List<Announcement> findAllByInstructorId(Long instructorId);
}
