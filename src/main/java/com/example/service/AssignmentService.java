package com.example.service;

import com.example.model.Assignment;
import com.example.model.Material;

import java.util.List;

public interface AssignmentService {
    void addAssignmentToCourse(Integer courseId, Assignment assignment);

    void removeAssignmentFromCourse(Integer courseId, Integer assignmentId);

    List<Assignment> getAssignmentsForCourse(Integer courseId);

    void addMaterialToAssignment(Integer assignmentId, Material material);

    void removeMaterialFromAssignment(Integer assignmentId, Integer materialId);
}
