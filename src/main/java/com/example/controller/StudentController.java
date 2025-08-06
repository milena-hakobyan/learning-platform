package com.example.controller;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponse> getStudent(@PathVariable Long studentId) {
        Optional<StudentResponse> response = studentService.getStudentById(studentId);
        return response.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{studentId}/courses")
    public ResponseEntity<List<CourseResponse>> getEnrolledCourses(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getEnrolledCourses(studentId));
    }

    @GetMapping("/{studentId}/submissions")
    public ResponseEntity<List<SubmissionResponse>> getSubmissions(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getSubmissionsByStudentId(studentId));
    }

    @GetMapping("/{studentId}/grades/course/{courseId}")
    public ResponseEntity<List<GradeResponse>> getGradesForCourse(@PathVariable Long studentId,
                                                                  @PathVariable Long courseId) {
        return ResponseEntity.ok(studentService.getGradesForCourse(courseId, studentId));
    }

    @GetMapping("/{studentId}/grades/assignment/{assignmentId}")
    public ResponseEntity<GradeResponse> getAssignmentGrade(@PathVariable Long studentId,
                                                            @PathVariable Long assignmentId) {
        return studentService.getAssignmentGradeForStudent(assignmentId, studentId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{studentId}/enroll/{courseId}")
    public ResponseEntity<Void> enrollInCourse(@PathVariable Long studentId,
                                               @PathVariable Long courseId) {
        studentService.enrollInCourse(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{studentId}/drop/{courseId}")
    public ResponseEntity<Void> dropCourse(@PathVariable Long studentId,
                                           @PathVariable Long courseId) {
        studentService.dropCourse(studentId, courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/courses/available")
    public ResponseEntity<List<CourseResponse>> browseAvailableCourses() {
        return ResponseEntity.ok(studentService.browseAvailableCourses());
    }

    @GetMapping("/{studentId}/materials/lesson/{lessonId}")
    public ResponseEntity<List<MaterialResponse>> accessMaterials(@PathVariable Long studentId,
                                                                  @PathVariable Long lessonId) {
        return ResponseEntity.ok(studentService.accessMaterials(studentId, lessonId));
    }

    @PostMapping("/{studentId}/submit/{assignmentId}")
    public ResponseEntity<Void> submitAssignment(@PathVariable Long studentId,
                                                 @PathVariable Long assignmentId,
                                                 @RequestBody CreateSubmissionRequest request) {
        studentService.submitAssignment(studentId, assignmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
