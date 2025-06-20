package com.example.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Course {
    private Integer courseId;
    private String title;
    private String description;
    private String category;
    private String url;
    private Integer instructorId;
    private final List<Lesson> lessons = new ArrayList<>();
    private final List<Assignment> assignments = new ArrayList<>();
    private final List<Student> enrolledStudents = new ArrayList<>();
    private final List<Announcement> announcements = new ArrayList<>();

    public Course(Integer courseId, String title, String description, String category, String url, Integer instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.instructorId = instructorId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public List<Lesson> getLessons() {
        return Collections.unmodifiableList(lessons);
    }

    public List<Assignment> getAssignments() {
        return Collections.unmodifiableList(assignments);
    }

    public List<Student> getEnrolledStudents() {
        return Collections.unmodifiableList(enrolledStudents);
    }

    public List<Announcement> getAnnouncements() {
        return Collections.unmodifiableList(announcements);
    }

    public void setLessons(List<Lesson> newLessons) {
        this.lessons.clear();
        this.lessons.addAll(newLessons);
    }

    public void addLesson(Lesson lesson) {
        if (!lessons.contains(lesson)) {
            lessons.add(lesson);
        }
    }

    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
    }

    public void addAssignment(Assignment assignment) {
        if (assignment != null && !assignments.contains(assignment)) {
            assignments.add(assignment);
        }
    }

    public void removeAssignment(Assignment assignment) {
        assignments.remove(assignment);
    }

    public void enrollStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
        }
    }

    public void removeStudent(Student student) {
        enrolledStudents.remove(student);
    }

    public void postAnnouncement(Announcement msg) {
        announcements.add(msg);
    }


    @Override
    public String toString() {
        return "Course {" +
                "courseId='" + courseId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", link='" + url + '\'' +
                ", instructorId='" + instructorId + '\'' +
                ", lessonsCount=" + lessons.size() +
                ", assignmentsCount=" + assignments.size() +
                ", enrolledStudentsCount=" + enrolledStudents.size() +
                ", announcementsCount=" + announcements.size() +
                '}';
    }

}