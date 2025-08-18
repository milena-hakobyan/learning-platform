package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "content_description")
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "lesson_materials",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id")
    )
    private List<Material> materials = new ArrayList<>();

    public Lesson(){}

    public Lesson(Long lessonId, String title, String content, Course course, LocalDateTime uploadedAt) {
        this.id = lessonId;
        this.title = title;
        this.content = content;
        this.course = course;
        this.uploadedAt = uploadedAt;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public List<Material> getMaterials() {
        return Collections.unmodifiableList(materials);
    }

    public void setMaterials(List<Material> materials) {
        this.materials = new ArrayList<>(materials);
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