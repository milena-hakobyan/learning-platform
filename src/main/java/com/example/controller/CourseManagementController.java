package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import com.example.service.CourseManagementService;
import com.example.specification.SearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseManagementController {

    private final CourseManagementService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getById(courseId));
    }


    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody CreateCourseRequest request) {
        CourseResponse created = courseService.createCourse(request);

        URI location = URI.create("/api/courses/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }



    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId,
                                                       @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, request));
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }





    @GetMapping("/{courseId}/announcements")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getAnnouncementsForCourse(courseId));
    }
}
