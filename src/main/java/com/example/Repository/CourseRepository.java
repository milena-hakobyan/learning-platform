package com.example.Repository;

import com.example.Model.Course;
import com.example.Model.Student;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Integer> {
    Optional<Course> findByTitle(String title);

    List<Course> findByInstructor(Integer instructorId);

    List<Course> findByCategory(String category);

    List<Student> findEnrolledStudents(Integer courseId);

    void enrollStudent(Integer courseId, Student student);

    boolean verifyStudentAccessToCourse(Integer studentId, Integer courseId);
}
