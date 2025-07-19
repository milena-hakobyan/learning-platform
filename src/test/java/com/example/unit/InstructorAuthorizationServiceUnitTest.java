package com.example.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.Course;
import com.example.service.CourseManagementService;
import com.example.service.InstructorAuthorizationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorAuthorizationServiceUnitTest {

    @Mock
    private CourseManagementService courseService;

    @InjectMocks
    private InstructorAuthorizationServiceImpl authService;

    @Test
    void ensureAuthorizedCourseAccess_shouldReturnCourse_whenInstructorIsAuthorized() {
        Integer instructorId = 10;
        Integer courseId = 20;

        Course mockCourse = new Course(courseId, "Java", "desc", "Programming", "url", instructorId);
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(mockCourse));

        Course result = authService.ensureAuthorizedCourseAccess(instructorId, courseId);

        assertEquals(mockCourse, result);
        verify(courseService).getCourseById(courseId);
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldThrowException_whenCourseNotFound() {
        Integer instructorId = 10;
        Integer courseId = 99;

        when(courseService.getCourseById(courseId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.ensureAuthorizedCourseAccess(instructorId, courseId)
        );

        assertEquals("Course not found with ID: 99", exception.getMessage());
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldThrowSecurityException_whenInstructorIsNotAuthorized() {
        Integer instructorId = 10;
        Integer courseId = 20;

        Course mockCourse = new Course(courseId, "Spring", "desc", "Programming", "url", 999); // different instructor
        when(courseService.getCourseById(courseId)).thenReturn(Optional.of(mockCourse));

        SecurityException exception = assertThrows(
                SecurityException.class,
                () -> authService.ensureAuthorizedCourseAccess(instructorId, courseId)
        );

        assertEquals("Instructor is not authorized to access this course", exception.getMessage());
    }
}
