package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {
    Optional<Student> getStudentById(Integer studentId);

    List<Course> getEnrolledCourses(Integer studentId);

    List<Submission> getSubmissionsByStudentId(Integer studentId);

    List<Course> browseAvailableCourses();

    List<Material> accessMaterials(Integer studentId, Integer courseId);

    Map<Assignment, Grade> getGradesForCourse(Integer courseId, Integer studentId);

    Optional<Grade> getAssignmentGradeForStudent(Integer assignmentId, Integer studentId);

    void enrollInCourse(Integer studentId, Integer courseId);

    void dropCourse(Integer studentId, Integer courseId);

    void submitAssignment(Integer submissionId, Integer studentId, Integer assignmentId, String content);
}
