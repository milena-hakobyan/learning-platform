package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import com.example.service.CourseManagementService;
import com.example.specification.SearchCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseManagementController {

    private final CourseManagementService courseService;


    @GetMapping("/{courseId}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getById(courseId));
    }

    @GetMapping
    public ResponseEntity<Page<CourseResponse>> getCourses(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(required = false) String createdBefore,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchCriteria> filters = new ArrayList<>();

        if (title != null) filters.add(new SearchCriteria("title", ":", title));
        if (category != null) filters.add(new SearchCriteria("category", ":", category));
        if (url != null) filters.add(new SearchCriteria("url", ":", url));

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        if (createdAfter != null) filters.add(new SearchCriteria("createdAt", ">", LocalDateTime.parse(createdAfter, formatter)));
        if (createdBefore != null) filters.add(new SearchCriteria("createdAt", "<", LocalDateTime.parse(createdBefore, formatter)));

        Page<CourseResponse> filteredCourses = courseService.getFilteredCourses(filters, pageable);
        return ResponseEntity.ok(filteredCourses);
    }




    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseResponse created = courseService.createCourse(request);

        URI location = URI.create("/api/courses/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }



    @PutMapping("/{courseId}")
    public ResponseEntity<CourseResponse> updateCourse(@PathVariable Long courseId,
                                                       @Valid @RequestBody UpdateCourseRequest request) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, request));
    }

    @DeleteMapping("/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
    }




    @GetMapping("/{courseId}/announcements")
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncements(@PathVariable Long courseId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(courseService.getAnnouncementsForCourse(courseId, pageable));
    }
}
