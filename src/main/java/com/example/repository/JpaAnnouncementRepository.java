package com.example.repository;

import com.example.model.ActivityLog;
import com.example.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByCourseId(Long courseId);

    List<Announcement> findAllByInstructorId(Long instructorId);
}
