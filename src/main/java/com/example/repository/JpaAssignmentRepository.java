package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByCourseId(Long courseId);

    @Query("""
                SELECT a
                FROM Assignment a
                JOIN a.course c
                WHERE c.instructor.id = :instructorId
            """)
    List<Assignment> findAllByInstructorId(Long instructorId);

    List<Assignment> findAllByDueDateBefore(LocalDateTime date);

    Optional<Assignment> findByTitle(String title);

    @Query("SELECT a.materials FROM Assignment a WHERE a.id = :id")
    List<Material> findMaterialsByAssignmentId(@Param("id") Long assignmentId);
}
