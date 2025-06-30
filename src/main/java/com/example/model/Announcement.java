package com.example.model;

import java.time.LocalDateTime;

public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private Integer instructorId;
    private LocalDateTime postedAt;
    private Integer courseId;

    public Announcement(Integer announcementId, String title, String content, Integer instructorId, Integer courseId, LocalDateTime postedAt) {
        this.id = announcementId;
        this.title = title;
        this.content = content;
        this.instructorId = instructorId;
        this.courseId = courseId;
        this.postedAt = postedAt;
    }

    public Announcement(Integer announcementId, String title, String content, Integer instructorId, Integer courseId) {
        this(announcementId, title, content, instructorId, courseId, null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getPostedById() {
        return instructorId;
    }

    public void setPostedBy(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public LocalDateTime getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(LocalDateTime postedAt) {
        this.postedAt = postedAt;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourse(Integer courseId) {
        this.courseId = courseId;
    }
}
