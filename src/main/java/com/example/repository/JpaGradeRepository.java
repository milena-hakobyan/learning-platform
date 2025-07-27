package com.example.repository;

import com.example.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaGradeRepository extends JpaRepository<Grade, Long> {

    List<Grade> findAllByStudentId(Long studentId);

    Optional<Grade> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("""
        SELECT g FROM Grade g
        WHERE g.student.id = :studentId AND g.assignment.course.id = :courseId
    """)
    List<Grade> findAllByStudentIdAndCourseId(Long studentId, Long courseId);
}
