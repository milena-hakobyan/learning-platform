package com.example.Repository;

import com.example.Model.Course;
import com.example.Model.Student;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, String> {
    Course findByTitle(String title);

    void enrollStudent(String courseId, Student student);

    List<Course> findByInstructor(String instructorId);

    List<Course> findByCategory(String category);

    List<Course> findByTag(List<String> tags);
}
