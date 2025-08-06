package com.example.service;

import com.example.model.Assignment;
import com.example.model.Material;

import java.util.List;

public interface AssignmentService {
    void addAssignmentToCourse(Long courseId, Assignment assignment);

    void removeAssignmentFromCourse(Long courseId, Long assignmentId);

    List<Assignment> getAssignmentsForCourse(Long courseId);

    void addMaterialToAssignment(Long assignmentId, Material material);

    void removeMaterialFromAssignment(Long assignmentId, Long materialId);
}
