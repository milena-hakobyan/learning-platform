package com.example.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<Material> materials;

    public Lesson(String title, String content) {
        this.lessonId = UUID.randomUUID().toString();
        this.title = title;
        this.content = content;
        this.materials = new ArrayList<>();
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
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
                "lessonId='" + lessonId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}