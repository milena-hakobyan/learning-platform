package com.example.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Announcement {
    private String announcementId;
    private String title;
    private String content;
    private String instructorId;
    private LocalDateTime postedAt;
    private Course course;

    public Announcement(String title, String content, String instructorId, Course course) {
        this.announcementId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.instructorId = instructorId;
        this.postedAt = LocalDateTime.now();
        this.course = course;
    }

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPostedById() {
        return instructorId;
    }

    public void setPostedBy(String instructorId) {
        this.instructorId = instructorId;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
