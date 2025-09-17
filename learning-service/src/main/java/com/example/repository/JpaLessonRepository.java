package com.example.repository;

import com.example.model.Lesson;
import com.example.model.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaLessonRepository extends JpaRepository<Lesson, Long> {
    Page<Lesson> findAll(Pageable pageable);

    Page<Lesson> findAllByCourseId(Long courseId, Pageable pageable);

    @Query("SELECT l FROM Lesson l WHERE l.course.instructor.id = :instructorId")
    Page<Lesson> findAllByInstructorId(Long instructorId, Pageable pageable);

    @Query("SELECT m FROM Lesson l JOIN l.materials m WHERE l.id = :lessonId")
    Page<Material> findAllMaterialsByLessonId(@Param("lessonId") Long lessonId, Pageable pageable);


    @Query("""
              SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
              FROM Lesson l
              JOIN l.course c
              JOIN c.enrolledStudents s
              WHERE s.id = :studentId AND l.id = :lessonId
            """)
    boolean existsByStudentIdAndLessonId(@Param("studentId") Long studentId, @Param("lessonId") Long lessonId);

}

