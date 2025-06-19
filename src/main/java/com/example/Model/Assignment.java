package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Assignment {
    private Integer assignmentId;
    private String title;
    private Integer courseId;
    private String description;
    private LocalDateTime dueDate;
    private double maxScore;
    private final List<Material> materials = new ArrayList<>();

    public Assignment(Integer assignmentId, String title, Integer courseId, String description, LocalDateTime dueDate, double maxScore) {
        this.assignmentId = assignmentId;
        this.title = title;
        this.courseId = courseId;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
    }

    public Assignment(String title, Integer courseId, String description, LocalDateTime dueDate, double maxScore) {
        this(null, title, courseId, description, dueDate, maxScore);
    }

    public Integer getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Integer assignmentId) {
        this.assignmentId = assignmentId;
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
                "assignmentId='" + assignmentId + '\'' +
                ", title='" + title + '\'' +
                ", dueDate=" + dueDate +
                ", maxScore=" + maxScore +
                '}';
    }
}
