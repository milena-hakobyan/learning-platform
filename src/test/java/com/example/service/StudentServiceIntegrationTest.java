package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class StudentServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer instructorId;
    private Integer courseId;
    private Integer studentId;
    private Integer assignmentId;
    private Integer lessonId;

    @BeforeEach
    void setup() {
        Instructor instructor = new Instructor(null, "janedoe", "Jane", "Doe",
                "jane@example.com", "pass456", LocalDateTime.now(), true,
                "Expert", 5, 4.9, true);
        instructor = instructorRepository.save(instructor);
        instructorId = instructor.getId();

        Course course = new Course(null, "Spring Boot Testing", "Test course", "Backend",
                "http://spring.io", instructorId);
        course = courseRepository.save(course);
        courseId = course.getId();

        Student student = new Student(null, "johnsmith", "John", "Smith",
                "johnsmith@example.com", "password", LocalDateTime.now(), true,
                0.0, 0, 0);
        student = studentRepository.save(student);
        studentId = student.getId();

        Lesson lesson = new Lesson(null, "Lesson 1", "Content 1", courseId, LocalDateTime.now());
        lesson = lessonRepository.save(lesson);
        lessonId = lesson.getId();

        Assignment assignment = new Assignment(null, "Assignment 1", "Desc", LocalDateTime.now().plusDays(7), 100, courseId);
        assignment = assignmentRepository.save(assignment);
        assignmentId = assignment.getId();
    }

    @AfterEach
    void cleanup() {
        dbConnection.execute("TRUNCATE TABLE submissions, assignments, lessons, courses, instructors, students, users, activity_logs RESTART IDENTITY CASCADE");
    }

    @Test
    void getStudentById_shouldReturnStudent() {
        var studentOpt = studentService.getStudentById(studentId);
        assertTrue(studentOpt.isPresent());
        assertEquals("johnsmith", studentOpt.get().getUserName());
    }

    @Test
    void enrollInCourse_shouldEnrollStudentAndLogActivity() {
        studentService.enrollInCourse(studentId, courseId);

        var enrolledCourses = studentService.getEnrolledCourses(studentId);
        assertTrue(enrolledCourses.stream().anyMatch(c -> c.getId().equals(courseId)));
    }

    @Test
    void submitAssignment_shouldSaveSubmissionAndLogActivity() {
        studentService.enrollInCourse(studentId, courseId); // enroll first

        studentService.submitAssignment(null, studentId, assignmentId, "http://submission.link");

        var submissions = studentService.getSubmissionsByStudentId(studentId);
        assertFalse(submissions.isEmpty());
        assertEquals(assignmentId, submissions.get(0).getAssignmentId());
    }

    @Test
    void accessMaterials_shouldReturnMaterialsAndLogActivity() {
        Material material = new Material(null, "Material 1", "pdf", "category", "http://material.link",instructorId, LocalDateTime.now());
        lessonRepository.addMaterial(lessonId, material);

        studentService.enrollInCourse(studentId, courseId);

        List<Material> materials = studentService.accessMaterials(studentId, lessonId);

        assertFalse(materials.isEmpty());
        assertEquals("Material 1", materials.get(0).getTitle());
    }

    @Test
    void dropCourse_shouldRemoveEnrollmentAndLogActivity() {
        studentService.enrollInCourse(studentId, courseId);
        studentService.dropCourse(studentId, courseId);

        var enrolledCourses = studentService.getEnrolledCourses(studentId);
        assertFalse(enrolledCourses.stream().anyMatch(c -> c.getId().equals(courseId)));
    }
}
