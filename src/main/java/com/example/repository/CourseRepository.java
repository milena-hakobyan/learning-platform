package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {
    Optional<Course> findByTitle(String title);

    List<Course> findAllByInstructor(Integer instructorId);

    List<Course> findAllByCategory(String category);

    List<Student> findEnrolledStudents(Integer courseId);

    void enrollStudent(Integer courseId, Integer studentId);

    void unenrollStudent(Integer courseId, Integer studentId);

    boolean isStudentEnrolled(Integer studentId, Integer courseId);

    void ensureCourseExists(Integer courseId);
}
