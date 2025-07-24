package com.example.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private String category;

    private String url;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;

    @OneToMany(mappedBy = "course")
    private final List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private final List<Assignment> assignments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "enrollments",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private final List<Student> enrolledStudents = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    private final List<Announcement> announcements = new ArrayList<>();

    public Course() {
    }

    public Course(Long id, String title, String description, String category, String url, Instructor instructor) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
        this.instructor = instructor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructorId) {
        this.instructor = instructorId;
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
            lesson.setCourse(this);
        }
    }

    public void removeLesson(Lesson lesson) {
        if (lessons.remove(lesson)) {
            lesson.setCourse(null);
        }
    }

    public void addAssignment(Assignment assignment) {
        if (assignment != null && !assignments.contains(assignment)) {
            assignments.add(assignment);
            assignment.setCourse(this);
        }
    }

    public void removeAssignment(Assignment assignment) {
        if (assignments.remove(assignment)) {
            assignment.setCourse(null);
        }
    }

    public void enrollStudent(Student student) {
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            student.getEnrolledCourses().add(this);
        }
    }

    public void removeStudent(Student student) {
        if (enrolledStudents.remove(student)) {
            student.getEnrolledCourses().remove(this);
        }
    }

    public void postAnnouncement(Announcement msg) {
        if (!announcements.contains(msg)) {
            announcements.add(msg);
            msg.setCourse(this);
        }
    }


    @Override
    public String toString() {
        return "Course {" +
                "courseId='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", link='" + url + '\'' +
                ", instructorId='" + instructor.getId() + '\'' +
                ", lessonsCount=" + lessons.size() +
                ", assignmentsCount=" + assignments.size() +
                ", enrolledStudentsCount=" + enrolledStudents.size() +
                ", announcementsCount=" + announcements.size() +
                '}';
    }

}