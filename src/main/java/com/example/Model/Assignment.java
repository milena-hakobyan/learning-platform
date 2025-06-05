package com.example.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Assignment {
    private String assignmentId;
    private String title;
    private String courseId;
    private String description;
    private LocalDateTime dueDate;
    private double maxScore;
    private List<Material> materials;

    public Assignment(String title, String courseId, String description, LocalDateTime dueDate, double maxScore) {
        this.assignmentId = UUID.randomUUID().toString();
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.materials = new ArrayList<>();
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
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

    public void setMaterials(List<Material> materials) {
        this.materials = new ArrayList<>(materials); // Defensive copy
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
