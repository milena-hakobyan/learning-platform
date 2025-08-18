package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentResponse getStudentById(Long studentId);

    StudentResponse updateStudent(Long studentId, UpdateStudentRequest request);

    List<CourseResponse> getEnrolledCourses(Long studentId);

    List<SubmissionResponse> getSubmissionsByStudentId(Long studentId);

    List<CourseResponse> browseAvailableCourses();

    List<MaterialResponse> accessLessonMaterials(Long studentId, Long lessonId);

    List<MaterialResponse> accessAssignmentMaterials(Long studentId, Long assignmentId);

    List<GradeResponse> getGradesForCourse(Long courseId, Long studentId);

    Optional<GradeResponse> getAssignmentGradeForStudent(Long assignmentId, Long studentId);

    void enrollInCourse(Long studentId, Long courseId);

    void dropCourse(Long studentId, Long courseId);

    void submitAssignment(Long studentId, CreateSubmissionRequest request);

    boolean hasSubmitted(Long studentId, Long assignmentId);
}
