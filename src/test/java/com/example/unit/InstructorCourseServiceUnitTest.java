package com.example.unit;

import com.example.model.Course;
import com.example.repository.ActivityLogRepository;
import com.example.repository.InstructorRepository;
import com.example.service.CourseManagementService;
import com.example.service.InstructorAuthorizationService;
import com.example.service.InstructorCourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorCourseServiceUnitTest {

    @Mock
    private ActivityLogRepository activityLogRepo;
    @Mock
    private CourseManagementService courseService;
    @Mock
    private InstructorRepository instructorRepo;
    @Mock
    private InstructorAuthorizationService instructorAuthService;

    @InjectMocks
    InstructorCourseServiceImpl instructorService;

    @Test
    void createCourse_shouldDelegateToCourseService_andLogCreation() {
        Course course = new Course(
                1,
                "Java Fundamentals",
                "Intro to Java basics",
                "Programming",
                "http://example.com/java",
                42
        );

        doNothing().when(courseService).createCourse(course);

        instructorService.createCourse(course);

        verify(courseService).createCourse(course);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(42) &&
                        log.getAction().equals("Created course: Java Fundamentals")
        ));
    }

    @Test
    void deleteCourse_shouldVerifyAccess_thenDelete_andLog() {
        Integer courseId = 101;
        Integer instructorId = 202;
        Course course = new Course(
                courseId,
                "Spring Boot Masterclass",
                "Deep dive into Spring Boot",
                "Programming",
                "http://example.com/spring-boot",
                instructorId
        );

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(instructorAuthService.ensureAuthorizedCourseAccess(instructorId, courseId)).thenReturn(course);
        doNothing().when(courseService).deleteCourse(courseId);

        instructorService.deleteCourse(instructorId, courseId);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorAuthService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verify(courseService).deleteCourse(courseId);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().equals("Deleted course: Spring Boot Masterclass")
        ));
    }

    @Test
    void deleteCourse_shouldThrow_whenInstructorDoesNotExist() {
        Integer instructorId = 999;
        Integer courseId = 123;

        doThrow(new IllegalArgumentException("Instructor not found"))
                .when(instructorRepo).ensureInstructorExists(instructorId);

        assertThrows(IllegalArgumentException.class, () ->
                instructorService.deleteCourse(instructorId, courseId)
        );

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verifyNoMoreInteractions(courseService, activityLogRepo, instructorAuthService);
    }

    @Test
    void deleteCourse_shouldThrow_whenInstructorUnauthorized() {
        Integer instructorId = 999;
        Integer courseId = 888;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doThrow(new SecurityException("Unauthorized"))
                .when(instructorAuthService).ensureAuthorizedCourseAccess(instructorId, courseId);

        assertThrows(SecurityException.class, () ->
                instructorService.deleteCourse(instructorId, courseId)
        );

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorAuthService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verifyNoInteractions(courseService);
        verifyNoInteractions(activityLogRepo);
    }

    @Test
    void getCoursesCreated_shouldReturnCoursesCreatedByInstructor() {
        Integer instructorId = 303;
        List<Course> courses = List.of(
                new Course(707, "Spring Boot Masterclass", "Deep dive into Spring Boot", "Programming", "http://example.com/spring-boot", instructorId)
        );

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(courseService.getCoursesByInstructor(instructorId)).thenReturn(courses);

        List<Course> result = instructorService.getCoursesCreated(instructorId);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(courseService).getCoursesByInstructor(instructorId);
        assertEquals(courses, result);
    }
}