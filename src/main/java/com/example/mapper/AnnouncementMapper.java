package com.example.mapper;

import com.example.dto.announcement.*;
import com.example.model.Announcement;
import com.example.model.Course;
import com.example.model.Instructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AnnouncementMapper {
    public AnnouncementResponse toDto(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setTitle(announcement.getTitle());
        response.setContent(announcement.getContent());
        response.setInstructorName(announcement.getInstructor().getUser().getFirstName() + " " + announcement.getInstructor().getUser().getLastName());
        response.setCourseTitle(announcement.getCourse().getTitle());
        response.setPostedAt(announcement.getPostedAt());
        return response;
    }

    public Announcement toEntity(CreateAnnouncementRequest dto, Instructor instructor, Course course) {
        Announcement announcement = new Announcement();
        announcement.setTitle(dto.getTitle());
        announcement.setContent(dto.getContent());
        announcement.setInstructor(instructor);
        announcement.setCourse(course);
        announcement.setPostedAt(LocalDateTime.now());
        return announcement;
    }

    public void updateEntity(UpdateAnnouncementRequest dto, Announcement announcement) {
        if (dto.getTitle() != null) {
            announcement.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            announcement.setContent(dto.getContent());
        }
    }

}
