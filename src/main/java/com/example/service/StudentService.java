package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {
    Optional<StudentResponse> getStudentById(Long studentId);

    List<CourseResponse> getEnrolledCourses(Long studentId);

    List<SubmissionResponse> getSubmissionsByStudentId(Long studentId);

    List<CourseResponse> browseAvailableCourses();

    List<MaterialResponse> accessMaterials(Long studentId, Long courseId);

    List<GradeResponse> getGradesForCourse(Long courseId, Long studentId);

    Optional<GradeResponse> getAssignmentGradeForStudent(Long assignmentId, Long studentId);

    void enrollInCourse(Long studentId, Long courseId);

    void dropCourse(Long studentId, Long courseId);

    void submitAssignment(Long studentId, Long assignmentId, CreateSubmissionRequest request);
}
