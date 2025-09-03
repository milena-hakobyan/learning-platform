package com.example.repository;

import com.example.model.Course;
import com.example.model.Lesson;
import com.example.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    Page<Course> findAll(Pageable pageable);

    Page<Course> findAll(Specification<Course> specification, Pageable pageable);

    Optional<Course> findByTitle(String title);

    Page<Course> findAllByInstructor_Id(Long instructorId, Pageable pageable);

    Page<Course> findAllByCategory(String category, Pageable pageable);

    @Query("SELECT s FROM Course c JOIN c.enrolledStudents s WHERE c.id = :courseId")
    Page<Student> findEnrolledStudents(@Param("courseId") Long courseId, Pageable pageable);

    // In CourseRepository
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.id = :id")
    Optional<Course> findByIdWithLessons(Long id);

    @Query("""
            SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
            FROM Course c JOIN c.enrolledStudents s
            WHERE c.id = :courseId AND s.id = :studentId
        """)
    boolean existsStudentEnrollment(Long courseId, Long studentId);
}