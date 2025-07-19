package com.example.service;

import com.example.model.Course;
import com.example.model.Instructor;
import com.example.repository.CourseRepository;
import com.example.repository.InstructorRepository;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class InstructorAuthorizationServiceIntegrationTest extends AbstractPostgresIntegrationTest{

    @Autowired
    private InstructorAuthorizationService authorizationService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer instructorId;
    private Integer courseId;

    @BeforeEach
    void setUp() {
        Instructor instructor = new Instructor(
                null,
                "janedoe",
                "Jane",
                "Doe",
                "jane@example.com",
                "pass123",
                LocalDateTime.now(),
                true,
                "Expert instructor",
                10,
                4.9,
                true
        );
        instructor = instructorRepository.save(instructor);
        instructorId = instructor.getId();

        Course course = new Course(
                null,
                "Test Course",
                "Test Description",
                "Category",
                "http://test.com",
                instructorId
        );
        course = courseRepository.save(course);
        courseId = course.getId();
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldThrowNullPointerException_whenInstructorIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                authorizationService.ensureAuthorizedCourseAccess(null, courseId)
        );
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldThrowNullPointerException_whenCourseIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                authorizationService.ensureAuthorizedCourseAccess(instructorId, null)
        );
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldThrowIllegalArgumentException_whenCourseNotFound() {
        Integer nonExistentCourseId = 9999;

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                authorizationService.ensureAuthorizedCourseAccess(instructorId, nonExistentCourseId)
        );

        assertEquals("Course not found with ID: " + nonExistentCourseId, ex.getMessage());
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldThrowSecurityException_whenInstructorNotAuthorized() {
        Instructor fakeInstructor = new Instructor(
                null,
                "fakeuser", "Fake", "Instructor",
                "fake@example.com", "pass",
                LocalDateTime.now(), true,
                "Fake bio", 1, 4.0, true
        );
        fakeInstructor = instructorRepository.save(fakeInstructor);
        Integer fakeInstructorId = fakeInstructor.getId();

        SecurityException ex = assertThrows(SecurityException.class, () ->
                authorizationService.ensureAuthorizedCourseAccess(fakeInstructorId, courseId)
        );

        assertEquals("Instructor is not authorized to access this course", ex.getMessage());
    }

    @Test
    void ensureAuthorizedCourseAccess_shouldReturnCourse_whenAuthorized() {
        Course course = authorizationService.ensureAuthorizedCourseAccess(instructorId, courseId);

        assertNotNull(course);
        assertEquals(courseId, course.getId());
        assertEquals(instructorId, course.getInstructorId());
    }
        @AfterEach
    void cleanup() {
        dbConnection.execute("TRUNCATE TABLE courses, instructors, users RESTART IDENTITY CASCADE");
    }
}