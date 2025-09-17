package com.example.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "instructors")
public class Instructor {

    @Id
    @Column(name = "user_id")
    private Long userId;

    private String bio;

    @Column(name = "total_courses_created")
    private int totalCoursesCreated;

    private double rating;

    @Column(name = "is_verified")
    private boolean isVerified;

    @OneToMany(mappedBy = "instructor")
    private List<Course> coursesCreated = new ArrayList<>();

    @OneToMany(mappedBy = "instructor")
    private List<Announcement> announcementsPosted = new ArrayList<>();

    public Instructor(Long userId, String bio) {
        this.userId = userId;
        this.bio = bio;
        this.totalCoursesCreated = 0;
        this.rating = 0.0;
        this.isVerified = false;
    }

    public void addCourse(Course course) {
        if (!coursesCreated.contains(course)) {
            coursesCreated.add(course);
            course.setInstructor(this);
        }
    }

    public void removeCourse(Course course) {
        if (coursesCreated.remove(course)) {
            course.setInstructor(null);
        }
    }

    public void addAnnouncement(Announcement announcement) {
        if (!announcementsPosted.contains(announcement)) {
            announcementsPosted.add(announcement);
            announcement.setInstructor(this);
        }
    }

    public void removeAnnouncement(Announcement announcement) {
        if (announcementsPosted.remove(announcement)) {
            announcement.setInstructor(null);
        }
    }

}