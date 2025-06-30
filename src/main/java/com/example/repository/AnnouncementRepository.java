package com.example.repository;

import com.example.model.Announcement;

import java.util.List;

public interface AnnouncementRepository extends CrudRepository<Announcement, Integer> {

    List<Announcement> findAllByCourseId(Integer courseId);

    List<Announcement> findAllByInstructorId(Integer instructorId);
}
