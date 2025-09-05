package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.exception.ResourceNotFoundException;
import com.example.feign.UserServiceClient;
import com.example.model.Course;
import com.example.repository.JpaInstructorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import feign.FeignException;

@Service
public class InstructorCourseServiceImpl implements InstructorCourseService {
    private final CourseManagementService courseService;
    private final JpaInstructorRepository instructorRepo;
    private final InstructorAuthorizationService instructorService;
    private final UserServiceClient userClient;

    public InstructorCourseServiceImpl(
            CourseManagementService courseService,
            JpaInstructorRepository instructorRepo,
            InstructorAuthorizationService instructorService,
            UserServiceClient userClient
    ) {
        this.courseService = courseService;
        this.instructorRepo = instructorRepo;
        this.instructorService = instructorService;
        this.userClient = userClient;
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest request) {
        CourseResponse courseResponse = courseService.createCourse(request);

        logActivity(request.getInstructorId(), "Created course: " + request.getTitle());

        return courseResponse;
    }

    @Override
    public void deleteCourse(Long instructorId, Long courseId) {
        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        courseService.deleteCourse(courseId);

        logActivity(instructorId, "Deleted course: " + course.getTitle());
    }

    @Override
    public Page<CourseResponse> getCoursesCreated(Long instructorId, Pageable pageable) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with ID: " + instructorId);
        }
        return courseService.getAllByInstructor(instructorId, pageable);
    }


    private void logActivity(Long userId, String action) {
        try {
            userClient.logActivity(userId, new UserServiceClient.ActivityLogRequest(action));
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
}
