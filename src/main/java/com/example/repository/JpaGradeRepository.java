package com.example.repository;

import com.example.model.Grade;
import com.example.model.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaGradeRepository extends JpaRepository<Grade, Long> {
    @Query("""
        SELECT g
        FROM Grade g
        JOIN g.submission s
        WHERE s.student.id = :studentId
    """)
    Page<Grade> findAllByStudentId(Long studentId, Pageable pageable);

    @Query("""
        SELECT g
        FROM Grade g
        JOIN g.submission s
        JOIN s.assignment a
        WHERE s.student.id = :studentId AND a.id = :assignmentId
    """)
    Optional<Grade> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    @Query("""
        SELECT g
        FROM Grade g
        JOIN g.submission s
        JOIN s.assignment a
        WHERE a.course.id = :courseId AND s.student.id = :studentId
    """)
    Page<Grade> findAllByStudentIdAndCourseId(Long studentId, Long courseId, Pageable pageable);
}
