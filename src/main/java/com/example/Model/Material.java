package com.example.Model;

import java.time.LocalDateTime;

public class Material {
    private Integer materialId;
    private String title;
    private String contentType;
    private String category;
    private String url;
    private Integer instructorId;
    private LocalDateTime uploadDate;

    public Material(Integer materialId, String title, String contentType, String category, String url, Integer instructorId, LocalDateTime uploadDate) {
        this.materialId = materialId;
        this.title = title;
        this.contentType = contentType;
        this.category = category;
        this.url = url;
        this.instructorId = instructorId;
        this.uploadDate = uploadDate;
    }

    public Integer getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Integer materialId) {
        this.materialId = materialId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
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
