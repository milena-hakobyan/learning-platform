package com.example.Service;

import com.example.Model.*;

import java.util.List;
import java.util.Map;

public interface StudentService extends UserService {
    List<Course> getEnrolledCourses(String studentId);

    void enrollInCourse(String studentId, String courseId);

    void dropCourse(String studentId, String courseId);

    List<Submission> getSubmissions(String studentId);

    List<Course> browseCourses(); // view all available courses

    List<Lesson> accessMaterials(String studentId, String courseId);

    void submitAssignment(String studentId, String assignmentId, String content);

    Map<Course, Map<Assignment, Grade>>  viewGrades(String studentId);
}
