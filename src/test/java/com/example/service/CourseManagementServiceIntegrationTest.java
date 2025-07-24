package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class CourseManagementServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private CourseManagementService courseManagementService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer studentId;
    private Integer instructorId;
    private Integer courseId;

    @BeforeEach
    void setup() {
        // Prepare an instructor for courses
        Instructor instructor = new Instructor(null, "janedoe", "Jane", "Doe",
                "jane@example.com", "pass456", LocalDateTime.now(), true,
                "Expert", 5, 4.9, true);
        instructor = instructorRepository.save(instructor);

        instructorId = instructor.getId();

        // Create a course for tests
        Course course = new Course(null, "Spring Boot Testing", "Test course", "Backend",
                "http://spring.io", instructorId);
        course = courseRepository.save(course);
        courseId = course.getId();

        Student student = new Student(null, "johnsmith", "John", "Smith",
                "johnsmith@example.com", "password", LocalDateTime.now(), true,
                0.0, 0, 0);
        student = studentRepository.save(student);
        studentId = student.getId();
    }

    @AfterEach
    void cleanup() {
        // Reset DB state after each test
        dbConnection.execute("TRUNCATE TABLE submissions, assignments, lessons, announcements, courses, instructors, users RESTART IDENTITY CASCADE");
    }

    @Test
    void createCourse_shouldSaveCourse() {
        Course newCourse = new Course(null, "New Course", "Description", "Category", "http://url", instructorId);

        courseManagementService.createCourse(newCourse);

        Optional<Course> saved = courseRepository.findByTitle("New Course");
        assertTrue(saved.isPresent());
        assertEquals("New Course", saved.get().getTitle());
    }

    @Test
    void createCourse_shouldThrow_whenCourseIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            courseManagementService.createCourse(null);
        });
        assertEquals("Course cannot be null", ex.getMessage());
    }

    @Test
    void createCourse_shouldThrow_whenTitleAlreadyExists() {
        Course duplicateCourse = new Course(null, "Spring Boot Testing", "Desc", "Cat", "http://url", instructorId);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            courseManagementService.createCourse(duplicateCourse);
        });

        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    void updateCourse_shouldSaveChanges() {
        Course course = courseRepository.findById(courseId).orElseThrow();
        course.setDescription("Updated description");

        courseManagementService.updateCourse(course);

        Course updated = courseRepository.findById(courseId).orElseThrow();
        assertEquals("Updated description", updated.getDescription());
    }

    @Test
    void updateCourse_shouldThrow_whenCourseIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            courseManagementService.updateCourse(null);
        });
        assertEquals("Course cannot be null", ex.getMessage());
    }

    @Test
    void updateCourse_shouldThrow_whenCourseDoesNotExist() {
        Course fakeCourse = new Course(9999, "Fake", "Desc", "Cat", "http://url", instructorId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            courseManagementService.updateCourse(fakeCourse);
        });
        assertEquals("Course with ID '9999' does not exist.", ex.getMessage());
    }

    @Test
    void deleteCourse_shouldDeleteCourseAndCascade() {
        // Setup: create assignment with submission for the course
        Assignment assignment = new Assignment(null, "Assignment 1", "Desc", LocalDateTime.now().plusDays(7), 100.0, courseId);
        assignment = assignmentRepository.save(assignment);

        Submission submission = new Submission(null, studentId, assignment.getId(), "http://content.link", LocalDateTime.now());
        submissionRepository.save(submission);

        // Announcements for course
        Announcement announcement = new Announcement(null, "Announcement 1", "Content", instructorId, courseId, LocalDateTime.now());
        announcementRepository.save(announcement);

        // Delete course, should delete assignments & submissions too
        courseManagementService.deleteCourse(courseId);

        assertFalse(courseRepository.findById(courseId).isPresent());
        assertTrue(assignmentRepository.findAllByCourseId(courseId).isEmpty());
        assertTrue(submissionRepository.findAllByAssignmentId(assignment.getId()).isEmpty());
        assertTrue(announcementRepository.findAllByCourseId(courseId).isEmpty());
    }

    @Test
    void getAnnouncementsForCourse_shouldReturnAnnouncements() {
        Announcement ann1 = new Announcement(null, "Ann 1", "Content 1", instructorId, courseId, LocalDateTime.now());
        announcementRepository.save(ann1);

        Announcement ann2 = new Announcement(null, "Ann 2", "Content 2", instructorId, courseId, LocalDateTime.now());
        announcementRepository.save(ann2);

        List<Announcement> announcements = courseManagementService.getAnnouncementsForCourse(courseId);

        assertEquals(2, announcements.size());
    }

    @Test
    void getAnnouncementsForCourse_shouldThrow_whenCourseDoesNotExist() {
        Integer fakeId = 9999;
        assertThrows(RuntimeException.class, () -> {
            courseManagementService.getAnnouncementsForCourse(fakeId);
        });
    }

    @Test
    void getCourseById_shouldReturnCourse() {
        Optional<Course> course = courseManagementService.getCourseById(courseId);
        assertTrue(course.isPresent());
        assertEquals(courseId, course.get().getId());
    }

    @Test
    void getCourseById_shouldThrow_whenIdIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            courseManagementService.getCourseById(null);
        });
        assertEquals("Course Id cannot be null", ex.getMessage());
    }

    @Test
    void getByIdWithLessons_shouldReturnCourseWithLessons() {
        Lesson lesson = new Lesson(null, "Lesson 1", "Content", courseId, LocalDateTime.now());
        lessonRepository.save(lesson);

        Optional<Course> courseWithLessons = courseManagementService.getByIdWithLessons(courseId);
        assertTrue(courseWithLessons.isPresent());
        assertEquals(1, courseWithLessons.get().getLessons().size());
    }

    @Test
    void getCoursesByInstructor_shouldReturnCourses() {
        List<Course> courses = courseManagementService.getCoursesByInstructor(instructorId);
        assertFalse(courses.isEmpty());
        assertTrue(courses.stream().anyMatch(c -> c.getInstructorId().equals(instructorId)));
    }

    @Test
    void getCoursesByInstructor_shouldThrow_whenIdIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            courseManagementService.getCoursesByInstructor(null);
        });
        assertEquals("InstructorId cannot be null", ex.getMessage());
    }

    @Test
    void getCoursesByCategory_shouldReturnCourses() {
        List<Course> courses = courseManagementService.getCoursesByCategory("Backend");
        assertFalse(courses.isEmpty());
        assertTrue(courses.stream().allMatch(c -> "Backend".equals(c.getCategory())));
    }

    @Test
    void getCoursesByCategory_shouldThrow_whenCategoryIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            courseManagementService.getCoursesByCategory(null);
        });
        assertEquals("Category cannot be null", ex.getMessage());
    }

    @Test
    void getCourseByTitle_shouldReturnCourse() {
        Optional<Course> course = courseManagementService.getCourseByTitle("Spring Boot Testing");
        assertTrue(course.isPresent());
    }

    @Test
    void getCourseByTitle_shouldThrow_whenTitleIsNull() {
        NullPointerException ex = assertThrows(NullPointerException.class, () -> {
            courseManagementService.getCourseByTitle(null);
        });
        assertEquals("Title cannot be null", ex.getMessage());
    }

    @Test
    void getAllCourses_shouldReturnList() {
        List<Course> allCourses = courseManagementService.getAllCourses();
        assertFalse(allCourses.isEmpty());
    }
}
