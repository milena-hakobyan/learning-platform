package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.AnnouncementMapper;
import com.example.mapper.CourseMapper;
import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseManagementServiceImpl implements CourseManagementService {

    private final JpaCourseRepository courseRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaAnnouncementRepository announcementRepo;
    private final JpaInstructorRepository instructorRepo;
    private final CourseMapper courseMapper;
    private final AnnouncementMapper announcementMapper;

    public CourseManagementServiceImpl(
            JpaCourseRepository courseRepo,
            JpaLessonRepository lessonRepo,
            JpaAssignmentRepository assignmentRepo,
            JpaSubmissionRepository submissionRepo,
            JpaAnnouncementRepository announcementRepo, JpaInstructorRepository instructorRepo, CourseMapper courseMapper, AnnouncementMapper announcementMapper
    ) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.announcementRepo = announcementRepo;
        this.instructorRepo = instructorRepo;
        this.courseMapper = courseMapper;
        this.announcementMapper = announcementMapper;
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest request) {
        if (request == null) throw new IllegalArgumentException("Course Create Request cannot be null");

        if (courseRepo.findByTitle(request.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Course with title '" + request.getTitle() + "' already exists.");
        }

        Instructor instructor = instructorRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        Course course = courseMapper.toEntity(request, instructor);
        Course saved = courseRepo.save(course);

        return courseMapper.toDto(saved);
    }


    @Override
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");
        if (request == null) throw new IllegalArgumentException("Course Update Request cannot be null");

        Course course = courseRepo.findById(courseId)
                        .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        courseMapper.updateEntity(request, course);

        courseRepo.save(course);

        return courseMapper.toDto(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));
        courseRepo.delete(course);
    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsForCourse(Long courseId) {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");

        if (!courseRepo.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        return announcementRepo.findAllByCourseId(courseId)
                .stream()
                .map(announcementMapper::toDto)
                .toList();
    }

    @Override
    public CourseResponse getById(Long courseId) {
        return courseRepo.findById(courseId)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    @Override
    public List<CourseResponse> getAllByInstructor(Long instructorId) {
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");

        return courseRepo.findAllByInstructor_Id(instructorId)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public List<CourseResponse> getAllByCategory(String category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");

        return courseRepo.findAllByCategory(category)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public CourseResponse getByTitle(String title) {
        return courseRepo.findByTitle(title)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with title: " + title));
    }

    @Override
    public List<CourseResponse> getAll() {
        return courseRepo.findAll()
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }
}