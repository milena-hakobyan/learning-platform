package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.feign.UserServiceClient;
import com.example.mapper.CourseMapper;
import com.example.mapper.MaterialMapper;
import com.example.repository.JpaLessonRepository;
import com.example.repository.JpaStudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import feign.FeignException;

@Service
public class StudentEnrollmentServiceImpl implements StudentEnrollmentService {
    private final JpaStudentRepository studentRepo;
    private final CourseManagementService courseManagementService;
    private final CourseEnrollmentService enrollmentService;
    private final CourseMapper courseMapper;
    private final UserServiceClient userClient;

    public StudentEnrollmentServiceImpl(
            JpaStudentRepository studentRepo,
            CourseManagementService courseManagementService,
            CourseEnrollmentService enrollmentService,
            CourseMapper courseMapper,
            UserServiceClient userClient
    ) {
        this.studentRepo = studentRepo;
        this.courseManagementService = courseManagementService;
        this.enrollmentService = enrollmentService;

        this.courseMapper = courseMapper;
        this.userClient = userClient;
    }

    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with Id: " + studentId + " doesn't exist");
        }

        CourseResponse courseResponse = courseManagementService.getById(courseId);

        enrollmentService.enrollStudent(courseId, studentId);
        logActivity(studentId, "Enrolled in course: " + courseResponse.getTitle());
    }

    @Override
    public void dropCourse(Long studentId, Long courseId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with Id: " + studentId + " doesn't exist");
        }

        CourseResponse courseResponse = courseManagementService.getById(courseId);

        enrollmentService.ensureStudentEnrollment(studentId, courseId);
        enrollmentService.unenrollStudent(courseId, studentId);
        logActivity(studentId, "Dropped course: " + courseResponse.getTitle());
    }

    @Override
    public Page<CourseResponse> browseAvailableCourses(Pageable pageable) {
        return courseManagementService.getAll(pageable);
    }

    @Override
    public Page<CourseResponse> getEnrolledCourses(Long studentId, Pageable pageable) {
        return studentRepo.findAllEnrolledCourses(studentId, pageable)
                .map(courseMapper::toDto);
    }

    /**
     * Logs activity through the User service.
     * Throws ResourceNotFoundException if user does not exist.
     */
    private void logActivity(Long userId, String action) {
        try {
            userClient.logActivity(userId, new UserServiceClient.ActivityLogRequest(action));
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
}
