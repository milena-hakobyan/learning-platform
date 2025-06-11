package com.example.Repository;

import com.example.Model.Announcement;

import java.util.List;

public interface AnnouncementRepository extends CrudRepository<Announcement, Integer> {

    List<Announcement> findByCourseId(Integer courseId);

    List<Announcement> findByInstructorId(Integer instructorId);
}
