package com.example.Repository;

import com.example.Model.Assignment;
import com.example.Model.Grade;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GradeRepository extends CrudRepository<Grade, Integer> {
    List<Grade> findGradesByStudentId(Integer studentId);

    Optional<Grade> findByAssignmentIdAndStudentId(Integer assignmentId, Integer studentId);

    Map<Assignment, Grade> findGradesByStudentIdForCourse(Integer studentId, Integer courseId);
}
