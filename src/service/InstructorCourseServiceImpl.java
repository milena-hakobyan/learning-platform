package com.example.service;

import com.example.model.ActivityLog;
import com.example.model.Course;
import com.example.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorCourseServiceImpl implements InstructorCourseService {
    private final ActivityLogRepository activityLogRepo;
    private final UserRepository userRepo;
    private final CourseManagementService courseService;
    private final InstructorRepository instructorRepo;
    private final InstructorAuthorizationService instructorService;


    public InstructorCourseServiceImpl(ActivityLogRepository activityLogRepo, UserRepository userRepo, CourseManagementService courseService, InstructorRepository instructorRepo, InstructorAuthorizationService instructorService) {
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
        instructorRepo.ensureInstructorExists(instructorId);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        courseService.deleteCourse(courseId);

        User user = userRepo.findById(course.getInstructor().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Deleted course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public List<Course> getCoursesCreated(Long instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return courseService.getCoursesByInstructor(instructorId);
    }
}