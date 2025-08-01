package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Material;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
    List<Assignment> findAllByCourseId(Integer courseId);

    List<Assignment> findAllAssignmentsByInstructorId(Integer instructorId);

    List<Assignment> findAllByDueDateBefore(LocalDateTime date);

    List<Material> findMaterialsByAssignmentId(Integer assignmentId);

    Optional<Assignment> findByTitle(String title);

    void addMaterial(Integer assignmentId, Material material);

    void removeMaterial(Integer assignmentId, Integer materialId);

    void ensureAssignmentExists(Integer assignmentId);
}