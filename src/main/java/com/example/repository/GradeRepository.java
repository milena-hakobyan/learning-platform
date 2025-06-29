package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Grade;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GradeRepository extends CrudRepository<Grade, Integer> {
    List<Grade> findAllGradesByStudentId(Integer studentId);

    Optional<Grade> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);

    Map<Assignment, Grade> findGradesByStudentIdForCourse(Integer studentId, Integer courseId);
}
