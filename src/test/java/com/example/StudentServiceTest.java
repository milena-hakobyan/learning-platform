package com.example;

import com.example.Model.*;
import com.example.Repository.*;
import com.example.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {
    private StudentService studentService;
    private InstructorService instructorService;
    private UserRepository userRepo;
    private AssignmentRepository assignmentRepo;
    private AnnouncementRepository announcementRepo;
    private SubmissionRepository submissionRepo;
    private CourseRepository courseRepo;
    private CourseService courseService;

    private Student student;
    private Course course;
    private Assignment assignment;

    @BeforeEach
    public void setUp() {
        userRepo = new InMemoryUserRepository();
        assignmentRepo = new InMemoryAssignmentRepository();
        submissionRepo = new InMemorySubmissionRepository();
        announcementRepo = new InMemoryAnnouncementRepository();
        courseRepo = new InMemoryCourseRepository();

        courseService = new CourseServiceImpl(courseRepo, assignmentRepo, submissionRepo);
        UserService userService = new UserServiceImpl(userRepo);
        studentService = new StudentServiceImpl(userService, courseService, assignmentRepo, submissionRepo);
        student = new Student("John Doe", "johnDoe", "john@example.com", "password123");
        userRepo.save(student);

        course = new Course("CS101", "Intro to Java", "Basic Java course", "Core", student.getUserId());
        courseRepo.save(course);

        assignment = new Assignment("Quiz 1", "CS101", "Simple quiz", LocalDateTime.now().plusDays(1), 100);
        assignmentRepo.save(assignment);
    }

    @Test
    public void testEnrollInCourseSuccessfully() {
        studentService.enrollInCourse(student.getUserId(), "CS101");

        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getUserId());
        assertEquals(1, enrolledCourses.size());
        assertEquals("CS101", enrolledCourses.get(0).getCourseId());
    }

    @Test
    public void testEnrollInNonexistentCourseThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                studentService.enrollInCourse(student.getUserId(), "NON_EXISTENT"));
        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    public void testSubmitAssignmentSuccessfully() {
        studentService.enrollInCourse(student.getUserId(), "CS101");
        studentService.submitAssignment(student.getUserId(), assignment.getAssignmentId(), "My solution");

        List<Submission> submissions = studentService.getSubmissions(student.getUserId());
        assertEquals(1, submissions.size());
        assertEquals("My solution", submissions.get(0).getContentLink());
    }

    @Test
    public void testDuplicateSubmissionThrowsException() {
        studentService.enrollInCourse(student.getUserId(), "CS101");
        studentService.submitAssignment(student.getUserId(), assignment.getAssignmentId(), "First solution");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                studentService.submitAssignment(student.getUserId(), assignment.getAssignmentId(), "Second solution"));

        assertEquals("Assignment already submitted by the student", ex.getMessage());
    }

    @Test
    public void testSubmitAssignmentWithoutEnrollmentThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                studentService.submitAssignment(student.getUserId(), assignment.getAssignmentId(), "Late submission"));

        assertEquals("Student not enrolled in the course", ex.getMessage());
    }

    @Test
    public void testDropCourseSuccessfully() {
        studentService.enrollInCourse(student.getUserId(), "CS101");
        studentService.dropCourse(student.getUserId(), "CS101");

        List<Course> enrolledCourses = studentService.getEnrolledCourses(student.getUserId());
        assertTrue(enrolledCourses.isEmpty());
    }

    @Test
    public void testDropNonexistentCourseThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                studentService.dropCourse(student.getUserId(), "NON_EXISTENT"));

        assertEquals("Course not found", ex.getMessage());
    }

    @Test
    public void testBrowseCoursesReturnsAllCourses() {
        Course anotherCourse = new Course("CS121", "Data Structures", "Learn about arrays, lists", "Core", student.getUserId());
        courseRepo.save(anotherCourse);

        List<Course> allCourses = studentService.browseCourses();
        assertEquals(2, allCourses.size());
    }

    @Test
    public void testAccessMaterialsWithEnrollment() {
        studentService.enrollInCourse(student.getUserId(), "CS101");

        List<Lesson> lessons = studentService.accessMaterials(student.getUserId(), "CS101");
        assertNotNull(lessons);
        assertEquals(course.getLessons(), lessons);
    }

    @Test
    public void testAccessMaterialsWithoutEnrollmentThrowsException() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                studentService.accessMaterials(student.getUserId(), "CS101"));

        assertEquals("Student not enrolled in the course", ex.getMessage());
    }

    @Test
    public void testGetSubmissionsReturnsEmptyListWhenNoneExist() {
        List<Submission> submissions = studentService.getSubmissions(student.getUserId());
        assertTrue(submissions.isEmpty());
    }


}
