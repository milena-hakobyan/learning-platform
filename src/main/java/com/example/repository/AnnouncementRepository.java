package com.example.repository;

import com.example.model.Announcement;

import java.util.List;

public interface AnnouncementRepository extends CrudRepository<Announcement, Integer> {

    List<Announcement> findByCourseId(Integer courseId);

    List<Announcement> findByInstructorId(Integer instructorId);
}
