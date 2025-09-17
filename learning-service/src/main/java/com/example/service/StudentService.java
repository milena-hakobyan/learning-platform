package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.instructor.InstructorResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface StudentService {
    StudentResponse getStudentById(Long studentId);

    StudentResponse updateStudent(Long studentId, UpdateStudentRequest request);

    Page<CourseResponse> getEnrolledCourses(Long studentId, Pageable pageable);

    Page<SubmissionResponse> getSubmissionsByStudentId(Long studentId, Pageable pageable);

    Page<CourseResponse> browseAvailableCourses(Pageable pageable);

    Page<GradeResponse> getGradesForCourse(Long courseId, Long studentId, Pageable pageable);

    Optional<GradeResponse> getAssignmentGradeForStudent(Long assignmentId, Long studentId);

    void enrollInCourse(Long studentId, Long courseId);

    void dropCourse(Long studentId, Long courseId);

    List<MaterialResponse> accessLessonMaterials(Long studentId, Long lessonId);

    List<MaterialResponse> accessAssignmentMaterials(Long studentId, Long assignmentId);

    void submitAssignment(Long studentId, CreateSubmissionRequest request);

    boolean hasSubmitted(Long studentId, Long assignmentId);

    Page<StudentResponse> getAllStudents(Pageable pageable);
}
