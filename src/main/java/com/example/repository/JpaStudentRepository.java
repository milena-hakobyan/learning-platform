package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaStudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s.enrolledCourses FROM Student s WHERE s.id = :studentId")
    List<Course> findAllEnrolledCourses(Long studentId);
}
