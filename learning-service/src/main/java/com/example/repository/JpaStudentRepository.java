package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaStudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findAll(Pageable pageable);

    @Query("SELECT c FROM Student s JOIN s.enrolledCourses c WHERE s.id = :studentId")
    Page<Course> findAllEnrolledCourses(@Param("studentId") Long studentId, Pageable pageable);
}
