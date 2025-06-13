package com.example.Repository;

import com.example.Model.Assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends CrudRepository<Assignment, String> {
    List<Assignment> findByCourseId(String courseId);

    List<Assignment> findByDueDateBefore(LocalDateTime date);

    Optional<Assignment> findByTitle(String title);
}
