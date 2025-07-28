package com.example.service;

import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.Student;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class CourseEnrollmentServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CourseEnrollmentService courseEnrollmentService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer courseId;
    private Integer studentId;

    @BeforeEach
    void setup() {
        Student student = new Student(
                null,
                "johndoe", "John", "Doe",
                "john@example.com", "pass123",
                LocalDateTime.now(), true,
                0.0, 0, 0
        );
        student = studentRepository.save(student);
        studentId = student.getId();


        Instructor instructor = new Instructor(
                null, "janedoe", "Jane", "Doe",
                "jane@example.com", "pass456",
                LocalDateTime.now(), true,
                "Spring expert", 3, 4.8, true
        );
        instructorRepository.save(instructor);

        Course course = new Course(
                null,
                "Spring Boot Intro",
                "Learn Spring Boot step by step",
                "Backend",
                "http://spring.io",
                instructor.getId()
        );
        course = courseRepository.save(course);
        courseId = course.getId();
    }

    @Test
    void enrollStudent_shouldSucceed_andBeFoundLater() {
        courseEnrollmentService.enrollStudent(courseId, studentId);

        List<Student> enrolled = courseEnrollmentService.getEnrolledStudents(courseId);

        assertEquals(1, enrolled.size());
        assertEquals(studentId, enrolled.get(0).getId());
        assertTrue(courseRepository.isStudentEnrolled(studentId, courseId));
    }

    @Test
    void enrollStudent_shouldFail_whenCourseDoesNotExist() {
        Integer nonExistentCourseId = 9999;

        assertThrows(RuntimeException.class, () -> {
            courseEnrollmentService.enrollStudent(nonExistentCourseId, studentId);
        });
    }

    @Test
    void unenrollStudent_shouldSucceed_andBeRemoved() {
        // Enroll first
        courseEnrollmentService.enrollStudent(courseId, studentId);

        // Confirm they are enrolled
        List<Student> enrolledBefore = courseEnrollmentService.getEnrolledStudents(courseId);
        assertEquals(1, enrolledBefore.size());

        // Unenroll
        courseEnrollmentService.unenrollStudent(courseId, studentId);

        // Confirm they are no longer enrolled
        List<Student> enrolledAfter = courseEnrollmentService.getEnrolledStudents(courseId);
        assertTrue(enrolledAfter.isEmpty());
        assertFalse(courseRepository.isStudentEnrolled(studentId, courseId));

    }


    @Test
    void getEnrolledStudents_shouldReturnEnrolledStudent() {
        // Enroll a student
        courseEnrollmentService.enrollStudent(courseId, studentId);

        // Call the method
        List<Student> students = courseEnrollmentService.getEnrolledStudents(courseId);

        // Assertions
        assertEquals(1, students.size());
        assertEquals(studentId, students.get(0).getId());
    }


    @Test
    void getEnrolledStudents_shouldThrow_whenCourseDoesNotExist() {
        Integer fakeCourseId = 9999;

        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            courseEnrollmentService.getEnrolledStudents(9999);
        });

    }

    @Test
    void ensureStudentEnrollment_shouldSucceed_whenStudentIsEnrolled() {
        courseEnrollmentService.enrollStudent(courseId, studentId);

        assertDoesNotThrow(() -> {
            courseEnrollmentService.ensureStudentEnrollment(studentId, courseId);
        });
    }

    @Test
    void ensureStudentEnrollment_shouldThrow_whenStudentIsNotEnrolled() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            courseEnrollmentService.ensureStudentEnrollment(studentId, courseId);
        });

        assertEquals("Student is not enrolled in the course", ex.getMessage());
    }

    @AfterEach
    void truncateTables() {
        dbConnection.execute("TRUNCATE TABLE enrollments, courses, students, instructors, users RESTART IDENTITY CASCADE");
    }
}