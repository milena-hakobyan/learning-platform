package com.example.service;

import com.example.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {
    Optional<Student> getStudentById(Long studentId);

    List<Course> getEnrolledCourses(Long studentId);

    List<Submission> getSubmissionsByStudentId(Long studentId);

    List<Course> browseAvailableCourses();

    List<Material> accessMaterials(Long studentId, Long courseId);

    List<Grade> getGradesForCourse(Long courseId, Long studentId);

    Optional<Grade> getAssignmentGradeForStudent(Long assignmentId, Long studentId);

    void enrollInCourse(Long studentId, Long courseId);

    void dropCourse(Long studentId, Long courseId);

    void submitAssignment(Long submissionId, Long studentId, Long assignmentId, String content);
}
