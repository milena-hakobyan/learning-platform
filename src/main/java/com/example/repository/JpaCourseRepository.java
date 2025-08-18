package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCourseRepository extends JpaRepository<Course, Long>{
    Optional<Course> findByTitle(String title);

    List<Course> findAllByInstructor_Id(Long instructorId);

    List<Course> findAllByCategory(String category);

    @Query("SELECT c.enrolledStudents FROM Course c WHERE c.id = :courseId")
    List<Student> findEnrolledStudents(Long courseId);

}