package com.example.Repository;

import com.example.Model.Announcement;

import java.util.List;

public interface AnnouncementRepository extends CrudRepository<Announcement, String> {

    List<Announcement> findByCourseId(String courseId);

    List<Announcement> findByInstructorId(String instructorId);
}
