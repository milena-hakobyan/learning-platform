package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.CourseMapper;
import com.example.model.ActivityLog;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.JpaActivityLogRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorCourseServiceImpl implements InstructorCourseService {
    private final JpaActivityLogRepository activityLogRepo;
    private final JpaUserRepository userRepo;
    private final CourseManagementService courseService;
    private final JpaInstructorRepository instructorRepo;
    private final InstructorAuthorizationService instructorService;
    private final CourseMapper courseMapper;


    public InstructorCourseServiceImpl(JpaActivityLogRepository activityLogRepo, JpaUserRepository userRepo, CourseManagementService courseService, JpaInstructorRepository instructorRepo, InstructorAuthorizationService instructorService, CourseMapper courseMapper) {
        this.activityLogRepo = activityLogRepo;
        this.userRepo = userRepo;
        this.courseService = courseService;
        this.instructorRepo = instructorRepo;
        this.instructorService = instructorService;
        this.courseMapper = courseMapper;
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest request) {
        CourseResponse courseResponse = courseService.createCourse(request);

        User user = userRepo.findById(request.getInstructorId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Created course: " + request.getTitle());
        activityLogRepo.save(activityLog);
        return courseResponse;
    }

    @Override
    public void deleteCourse(Long instructorId, Long courseId) {
        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        courseService.deleteCourse(courseId);

        User user = userRepo.findById(course.getInstructor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Deleted course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public Page<CourseResponse> getCoursesCreated(Long instructorId, Pageable pageable) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with ID: " + instructorId);
        }
        return courseService.getAllByInstructor(instructorId, pageable);
    }
}