package com.example.mapper;

import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.dto.material.UpdateMaterialRequest;
import com.example.model.Instructor;
import com.example.model.Material;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MaterialMapper {

    public Material toEntity(CreateMaterialRequest dto, Instructor instructor) {
        Material material = new Material();
        material.setTitle(dto.getTitle());
        material.setContentType(dto.getContentType());
        material.setCategory(dto.getCategory());
        material.setUrl(dto.getUrl());
        material.setInstructor(instructor);
        material.setUploadDate(LocalDateTime.now());
        return material;
    }

    public MaterialResponse toDto(Material material) {
        MaterialResponse response = new MaterialResponse();
        response.setId(material.getId());
        response.setTitle(material.getTitle());
        response.setContentType(material.getContentType());
        response.setCategory(material.getCategory());
        response.setUrl(material.getUrl());
        response.setUploadDate(material.getUploadDate());
        return response;
    }

    public void updateEntity(UpdateMaterialRequest dto, Material material) {
        if (dto.getTitle() != null) {
            material.setTitle(dto.getTitle());
        }
        if (dto.getContentType() != null) {
            material.setContentType(dto.getContentType());
        }
        if (dto.getCategory() != null) {
            material.setCategory(dto.getCategory());
        }
        if (dto.getUrl() != null) {
            material.setUrl(dto.getUrl());
        }
    }
}
