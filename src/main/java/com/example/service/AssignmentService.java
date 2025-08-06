package com.example.service;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;

import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(Long courseId, CreateAssignmentRequest request);

    AssignmentResponse updateAssignment(Long assignmentId, UpdateAssignmentRequest request);

    void deleteAssignment(Long assignmentId);

    AssignmentResponse getAssignmentById(Long assignmentId);

    List<AssignmentResponse> getAssignmentsForCourse(Long courseId);

    MaterialResponse addMaterialToAssignment(Long assignmentId, CreateMaterialRequest request);

    void removeMaterialFromAssignment(Long assignmentId, Long materialId);
}
