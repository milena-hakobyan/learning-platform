package com.example.service;

import com.example.model.ActivityLog;
import com.example.model.Course;
import com.example.model.User;
import com.example.repository.JpaActivityLogRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaUserRepository;
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


    public InstructorCourseServiceImpl(JpaActivityLogRepository activityLogRepo, JpaUserRepository userRepo, CourseManagementService courseService, JpaInstructorRepository instructorRepo, InstructorAuthorizationService instructorService) {
        this.activityLogRepo = activityLogRepo;
        this.userRepo = userRepo;
        this.courseService = courseService;
        this.instructorRepo = instructorRepo;
        this.instructorService = instructorService;
    }

    @Override
    public void createCourse(Course course) {
        courseService.createCourse(course);

        User user = userRepo.findById(course.getInstructor().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Created course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteCourse(Long instructorId, Long courseId) {
        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        courseService.deleteCourse(courseId);

        User user = userRepo.findById(course.getInstructor().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Deleted course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public List<Course> getCoursesCreated(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        return courseService.getCoursesByInstructor(instructorId);
    }
}