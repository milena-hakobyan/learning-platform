package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Integer>{
    List<Course> findAllEnrolledCourses(Integer studentId);

    void ensureStudentExists(Integer studentId);
}
