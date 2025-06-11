package com.example.Repository;

import com.example.Model.Assignment;
import com.example.Model.Material;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends CrudRepository<Assignment, Integer> {
    List<Assignment> findByCourseId(Integer courseId);

    List<Assignment> findAssignmentsByInstructorId(Integer instructorId);

    List<Assignment> findByDueDateBefore(LocalDateTime date);

    Optional<Assignment> findByTitle(String title);

    void addMaterial(Integer assignmentId, Material material);

    void removeMaterial(Integer assignmentId, Integer materialId);
}