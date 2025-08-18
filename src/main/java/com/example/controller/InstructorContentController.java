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
import lombok.RequiredArgsConstructor;
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
            @RequestBody CreateCourseRequest request) {

        CourseResponse created = courseService.createCourse(instructorId, request);
        URI location = URI.create(String.format("/api/courses/%d", created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}")
    @ResponseStatus
    public void deleteCourse(@PathVariable Long courseId, @PathVariable Long instructorId) {
        courseService.deleteCourse(courseId, instructorId);
    }


    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponse>> getCoursesCreated(@PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.getCoursesCreated(instructorId));
    }


    @PostMapping("/courses/{courseId}/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @RequestBody CreateAssignmentRequest request) {

        AssignmentResponse created = contentService.createAssignment(instructorId, courseId, request);
        URI location = URI.create(String.format("/api/courses/%d/assignments/%d", courseId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}/assignments/{assignmentId}")
    @ResponseStatus
    public void deleteAssignment(@PathVariable Long instructorId,
                                 @PathVariable Long courseId,
                                 @PathVariable Long assignmentId) {
        contentService.deleteAssignment(instructorId, courseId, assignmentId);
    }


    @PutMapping("/courses/{courseId}/assignments/{assignmentId}")
    public ResponseEntity<AssignmentResponse> updateAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long assignmentId,
            @RequestBody UpdateAssignmentRequest request) {

        return ResponseEntity.ok(contentService.updateAssignment(instructorId, assignmentId, request));
    }


    @PostMapping("/courses/{courseId}/lessons")
    public ResponseEntity<LessonResponse> createLesson(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @RequestBody CreateLessonRequest request) {

        LessonResponse created = contentService.createLesson(instructorId, courseId, request);
        URI location = URI.create(String.format("/api/courses/%d/lessons/%d", courseId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @PutMapping("/courses/{courseId}/lessons/{lessonId}")
    public ResponseEntity<LessonResponse> updateLesson(
            @PathVariable Long instructorId,
            @PathVariable Long lessonId,
            @RequestBody UpdateLessonRequest request) {

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
            @RequestBody CreateMaterialRequest request) {

        MaterialResponse created = contentService.addMaterialToLesson(instructorId, lessonId, request);
        URI location = URI.create(String.format("/api/lessons/%d/materials/%d", lessonId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}/lessons/{lessonId}/materials/{materialId}")
    @ResponseStatus
    public void deleteMaterialFromLesson(@PathVariable Long instructorId,
                                         @PathVariable Long lessonId,
                                         @PathVariable Long materialId) {
        contentService.deleteMaterialFromLesson(instructorId, lessonId, materialId);
    }


    @PostMapping("/courses/{courseId}/assignments/{assignmentId}/materials")
    public ResponseEntity<MaterialResponse> addMaterialToAssignment(
            @PathVariable Long instructorId,
            @PathVariable Long assignmentId,
            @RequestBody CreateMaterialRequest request) {

        MaterialResponse created = contentService.addMaterialToAssignment(instructorId, assignmentId, request);
        URI location = URI.create(String.format("/api/assignments/%d/materials/%d", assignmentId, created.getId()));
        return ResponseEntity.created(location).body(created);
    }


    @DeleteMapping("/courses/{courseId}/assignments/{assignmentId}/materials/{materialId}")
    @ResponseStatus
    public void deleteMaterialFromAssignment(@PathVariable Long instructorId,
                                             @PathVariable Long assignmentId,
                                             @PathVariable Long materialId) {
        contentService.deleteMaterialFromAssignment(instructorId, assignmentId, materialId);
    }
}