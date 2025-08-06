package com.example.repository;

import com.example.model.Lesson;
import com.example.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaLessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findAllByCourseId(Long courseId);

    @Query("SELECT l FROM Lesson l WHERE l.course.instructor.id = :instructorId")
    List<Lesson> findAllByInstructorId(Long instructorId);

    @Query("SELECT l.materials FROM Lesson l WHERE l.id = :lessonId")
    List<Material> findAllMaterialsByLessonId(Long lessonId);


    @Query("""
              SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
              FROM Lesson l
              JOIN l.course c
              JOIN c.enrolledStudents s
              WHERE s.id = :studentId AND l.id = :lessonId
            """)
    boolean existsByStudentIdAndLessonId(@Param("studentId") Long studentId, @Param("lessonId") Long lessonId);

}

