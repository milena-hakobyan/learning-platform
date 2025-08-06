package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import com.example.service.CourseManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseManagementController {

    private final CourseManagementService courseService;

    public CourseManagementController(CourseManagementService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CreateCourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId,
                                                       @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, request));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long courseId) {
        return courseService.getById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{courseId}/lessons")
    public ResponseEntity<CourseResponse> getCourseWithLessons(@PathVariable Long courseId) {
        return courseService.getByIdWithLessons(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/instructor/{instructorId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.getAllByInstructor(instructorId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(courseService.getAllByCategory(category));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<CourseResponse> getCourseByTitle(@PathVariable String title) {
        return courseService.getByTitle(title)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/{courseId}/announcements")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getAnnouncementsForCourse(courseId));
    }
}
