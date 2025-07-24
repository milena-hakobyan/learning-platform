package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private double maxScore;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "assignment_materials",
            joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id")
    )
    private final List<Material> materials = new ArrayList<>();


    public Assignment() {
    }

    public Assignment(Long assignmentId, String title, String description, LocalDateTime dueDate, double maxScore, Course course) {
        this.id = assignmentId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.course = course;

    }

    public Assignment(String title, String description, LocalDateTime dueDate, double maxScore, Course course) {
        this(null, title, description, dueDate, maxScore, course);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
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

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(double maxScore) {
        this.maxScore = maxScore;
    }

    public List<Material> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    public void addMaterial(Material material) {
        this.materials.add(material);
    }

    public void removeMaterial(Material material) {
        this.materials.remove(material);
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "assignmentId='" + id + '\'' +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", maxScore=" + maxScore +
                '}';
    }
}