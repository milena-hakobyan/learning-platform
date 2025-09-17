package com.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(name = "content_type", nullable = false)
    private String contentType;
    @Column(nullable = false)
    private String category;
    private String url;
    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructor instructor;
    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    public Material() {
    }

    public Material(Long materialId, String title, String contentType, String category, String url, Instructor instructor, LocalDateTime uploadDate) {
        this.id = materialId;
        this.title = title;
        this.contentType = contentType;
        this.category = category;
        this.url = url;
        this.instructor = instructor;
        this.uploadDate = uploadDate;
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

    public Instructor getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }
}
