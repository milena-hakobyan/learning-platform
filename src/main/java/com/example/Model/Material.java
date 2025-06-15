package com.example.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Material {
    private String materialId;
    private String title;
    private String type;
    private String url;
    private Instructor uploadedBy;
    private LocalDateTime uploadeDate;

    public Material(String title, String type, String url, Instructor uploadedBy, LocalDateTime uploadeDate) {
        this.materialId = UUID.randomUUID().toString();
        this.title = title;
        this.type = type;
        this.url = url;
        this.uploadedBy = uploadedBy;
        this.uploadeDate = uploadeDate;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instructor getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Instructor uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadeDate() {
        return uploadeDate;
    }

    public void setUploadeDate(LocalDateTime uploadeDate) {
        this.uploadeDate = uploadeDate;
    }
}
