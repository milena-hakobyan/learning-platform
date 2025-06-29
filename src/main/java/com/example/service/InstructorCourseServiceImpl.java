package com.example.service;

import com.example.model.ActivityLog;
import com.example.model.Course;
import com.example.repository.ActivityLogRepository;
import com.example.repository.InstructorRepository;

import java.util.List;

public class InstructorCourseServiceImpl implements InstructorCourseService {
    private final ActivityLogRepository activityLogRepo;
    private final CourseManagementService courseService;
    private final InstructorRepository instructorRepo;
    private final InstructorAuthorizationService instructorService;


    public InstructorCourseServiceImpl(ActivityLogRepository activityLogRepo, CourseManagementService courseService, InstructorRepository instructorRepo, InstructorAuthorizationService instructorService) {
        this.activityLogRepo = activityLogRepo;
        this.courseService = courseService;
        this.instructorRepo = instructorRepo;
        this.instructorService = instructorService;
    }

    @Override
    public void createCourse(Course course) {
        courseService.createCourse(course);

        activityLogRepo.save(new ActivityLog(course.getInstructorId(), "Created course: " + course.getTitle()));
    }

    @Override
    public void deleteCourse(Integer instructorId, Integer courseId) {
        instructorRepo.ensureInstructorExists(instructorId);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        courseService.deleteCourse(courseId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted course: " + course.getTitle()));
    }

    @Override
    public List<Course> getCoursesCreated(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return courseService.getCoursesByInstructor(instructorId);
    }


}
