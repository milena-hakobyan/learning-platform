package com.example.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lesson {
    private Integer id;
    private String title;
    private String content;
    private Integer courseId;
    LocalDateTime uploadDate;
    private List<Material> materials = new ArrayList<>();

    public Lesson(Integer lessonId, String title, String content, Integer courseId, LocalDateTime uploadDate) {
        this.id = lessonId;
        this.title = title;
        this.content = content;
        this.courseId = courseId;
        this.uploadDate = uploadDate;
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

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
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
        return "Lesson{" +
                "lessonId='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}