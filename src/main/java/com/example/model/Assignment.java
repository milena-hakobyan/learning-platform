package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Assignment {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private double maxScore;
    private final List<Material> materials = new ArrayList<>();

    private Integer courseId;


    public Assignment(Integer assignmentId, String title, String description, LocalDateTime dueDate, double maxScore, Integer courseId ) {
        this.id = assignmentId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.courseId = courseId;

    }

    public Assignment(String title, String description, LocalDateTime dueDate, double maxScore, Integer courseId) {
        this(null, title, description, dueDate, maxScore, courseId);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
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