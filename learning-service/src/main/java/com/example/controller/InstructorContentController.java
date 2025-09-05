package com.example.controller;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.service.InstructorContentService;
import com.example.service.InstructorCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}")
@RequiredArgsConstructor
public class InstructorContentController {

    private final InstructorContentService contentService;
    private final InstructorCourseService courseService;

    @PostMapping("/courses")
    public ResponseEntity<CourseResponse> createCourse(
            @PathVariable Long instructorId,
            @Valid @RequestBody CreateCourseRequest request) {

        CourseResponse created = courseService.createCourse(request);
        URI location = URI.create(String.format("/api/courses/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long courseId, @PathVariable Long instructorId) {
        courseService.deleteCourse(courseId, instructorId);
    }


    @GetMapping("/courses")
    public ResponseEntity<Page<CourseResponse>> getCoursesCreated(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CourseResponse> courses = courseService.getCoursesCreated(instructorId, pageable);
        return ResponseEntity.ok(courses);
    }


    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAssignmentRequest request) {

        AssignmentResponse created = contentService.createAssignment(instructorId, courseId, request);
        URI location = URI.create(String.format("/api/courses/%d/assignments/%d", courseId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}/assignments/{assignmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable Long instructorId,
                                 @PathVariable Long courseId,
                                 @PathVariable Long assignmentId) {
        contentService.deleteAssignment(instructorId, courseId, assignmentId);
    }


    @PutMapping("/courses/{courseId}/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody UpdateAssignmentRequest request) {

        return ResponseEntity.ok(contentService.updateAssignment(instructorId, assignmentId, request));
    }


    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<LessonResponse> createLesson(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @Valid @RequestBody CreateLessonRequest request) {

        LessonResponse created = contentService.createLesson(instructorId, courseId, request);
        URI location = URI.create(String.format("/api/courses/%d/lessons/%d", courseId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @PutMapping("/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long instructorId,
            @PathVariable Long lessonId,
            @Valid @RequestBody UpdateLessonRequest request) {

        return ResponseEntity.ok(contentService.updateLesson(instructorId, lessonId, request));
    }


    @DeleteMapping("/courses/{courseId}/lessons/{lessonId}")
    @ResponseStatus
    public void deleteLesson(@PathVariable Long instructorId,
                             @PathVariable Long courseId,
                             @PathVariable Long lessonId) {
        contentService.deleteLesson(instructorId, courseId, lessonId);
    }


    @PostMapping("/courses/{courseId}/lessons/{lessonId}/materials")
    public ResponseEntity<MaterialResponse> addMaterialToLesson(
            @PathVariable Long instructorId,
            @PathVariable Long lessonId,
            @Valid @RequestBody CreateMaterialRequest request) {

        MaterialResponse created = contentService.addMaterialToLesson(instructorId, lessonId, request);
        URI location = URI.create(String.format("/api/lessons/%d/materials/%d", lessonId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}/lessons/{lessonId}/materials/{materialId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaterialFromLesson(@PathVariable Long instructorId,
                                         @PathVariable Long lessonId,
                                         @PathVariable Long materialId) {
        contentService.deleteMaterialFromLesson(instructorId, lessonId, materialId);
    }


    @PostMapping("/courses/{courseId}/assignments/{assignmentId}/materials")
    public ResponseEntity<MaterialResponse> addMaterialToAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long assignmentId,
            @Valid @RequestBody CreateMaterialRequest request) {

        MaterialResponse created = contentService.addMaterialToAssignment(instructorId, assignmentId, request);
        URI location = URI.create(String.format("/api/assignments/%d/materials/%d", assignmentId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}/assignments/{assignmentId}/materials/{materialId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMaterialFromAssignment(@PathVariable Long instructorId,
                                             @PathVariable Long assignmentId,
                                             @PathVariable Long materialId) {
        contentService.deleteMaterialFromAssignment(instructorId, assignmentId, materialId);
    }
}