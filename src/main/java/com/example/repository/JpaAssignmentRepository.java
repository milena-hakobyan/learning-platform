package com.example.repository;

import com.example.model.Assignment;
import com.example.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAssignmentRepository extends JpaRepository<Assignment, Long> {

    Page<Assignment> findAllByCourseId(Long courseId, Pageable pageable);

    @Query("""
                SELECT a
                FROM Assignment a
                JOIN a.course c
                WHERE c.instructor.id = :instructorId
            """)
    Page<Assignment> findAllByInstructorId(Long instructorId, Pageable pageable);

    Page<Assignment> findAllByDueDateBefore(LocalDateTime date, Pageable pageable);

    Optional<Assignment> findByTitle(String title);

    @Query("SELECT m FROM Assignment a JOIN a.materials m WHERE a.id = :id")
    Page<Material> findMaterialsByAssignmentId(@Param("id") Long assignmentId, Pageable pageable);
}
