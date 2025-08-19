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
import com.example.specification.CourseSpecification;
import com.example.specification.SearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseManagementServiceImpl implements CourseManagementService {

    private final JpaCourseRepository courseRepo;
    private final JpaAnnouncementRepository announcementRepo;
    private final JpaInstructorRepository instructorRepo;
    private final CourseMapper courseMapper;
    private final AnnouncementMapper announcementMapper;

    public CourseManagementServiceImpl(
            JpaCourseRepository courseRepo,
            JpaAnnouncementRepository announcementRepo,
            JpaInstructorRepository instructorRepo,
            CourseMapper courseMapper,
            AnnouncementMapper announcementMapper) {
        this.courseRepo = courseRepo;
        this.announcementRepo = announcementRepo;
        this.instructorRepo = instructorRepo;
        this.courseMapper = courseMapper;
        this.announcementMapper = announcementMapper;
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest request) {
        if (courseRepo.findByTitle(request.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Course with title '" + request.getTitle() + "' already exists.");
        }

        Instructor instructor = instructorRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        Course course = courseMapper.toEntity(request, instructor);
        Course saved = courseRepo.save(course);

        return courseMapper.toDto(saved);
    }

    @Override
    public CourseResponse updateCourse(Long courseId, UpdateCourseRequest request) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        courseMapper.updateEntity(request, course);

        courseRepo.save(course);

        return courseMapper.toDto(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        courseRepo.delete(course);
    }

    @Override
    public Page<AnnouncementResponse> getAnnouncementsForCourse(Long courseId, Pageable pageable) {
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        return announcementRepo.findAllByCourseId(courseId, pageable)
                .map(announcementMapper::toDto);
    }

    @Override
    public CourseResponse getById(Long courseId) {
        return courseRepo.findById(courseId)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    @Override
    public CourseResponse getByIdWithLessons(Long courseId) {
        return courseRepo.findByIdWithLessons(courseId)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
    }

    @Override
    public Page<CourseResponse> getAllByInstructor(Long instructorId, Pageable pageable) {
        return courseRepo.findAllByInstructor_Id(instructorId, pageable)
                .map(courseMapper::toDto);
    }

    @Override
    public Page<CourseResponse> getAllByCategory(String category, Pageable pageable) {
        return courseRepo.findAllByCategory(category, pageable)
                .map(courseMapper::toDto);
    }

    @Override
    public CourseResponse getByTitle(String title) {
        return courseRepo.findByTitle(title)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with title: " + title));
    }

    @Override
    public Page<CourseResponse> getAll(Pageable pageable) {
        return courseRepo.findAll(pageable)
                .map(courseMapper::toDto);
    }

    public Specification<Course> buildSpecification(List<SearchCriteria> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        Specification<Course> result = new CourseSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = result.and(new CourseSpecification(params.get(i)));
        }

        return result;
    }

    @Override
    public Page<CourseResponse> getFilteredCourses(List<SearchCriteria> filters, Pageable pageable) {
        Specification<Course> spec = buildSpecification(filters);
        Page<Course> courses = courseRepo.findAll(spec, pageable);
        return courses.map(courseMapper::toDto);
    }
}
