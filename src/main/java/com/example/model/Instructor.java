package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instructors")
public class Instructor {

    @Id
    private Long id;

    @OneToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @MapsId
    private User user;

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

    public Instructor() {

    }

    public Instructor(User user, String bio) {
        this.user = user;
        this.bio = bio;
        this.totalCoursesCreated = 0;
        this.rating = 0.0;
        this.isVerified = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Announcement> getAnnouncementsPosted() {
        return announcementsPosted;
    }

    public void setAnnouncementsPosted(List<Announcement> announcementsPosted) {
        this.announcementsPosted = announcementsPosted;
    }

    public List<Course> getCoursesCreated() {
        return coursesCreated;
    }

    public void setCoursesCreated(List<Course> coursesCreated) {
        this.coursesCreated = coursesCreated;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getTotalCoursesCreated() {
        return totalCoursesCreated;
    }

    public void setTotalCoursesCreated(int totalCoursesCreated) {
        this.totalCoursesCreated = totalCoursesCreated;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
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