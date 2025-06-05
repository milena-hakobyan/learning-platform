package com.example;

import com.example.Model.*;
import com.example.Repository.*;
import com.example.Service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstructorServiceTest {
    private InstructorService instructorService;
    private StudentService studentService;

    private UserRepository userRepo;
    private AssignmentRepository assignmentRepo;
    private SubmissionRepository submissionRepo;
    private AnnouncementRepository announcementRepo;
    private CourseRepository courseRepo;

    private Instructor instructor;
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

        UserService userService = new UserServiceImpl(userRepo);
        CourseService courseService = new CourseServiceImpl(courseRepo, assignmentRepo, submissionRepo);

        instructorService = new InstructorServiceImpl(userService, courseService, assignmentRepo, submissionRepo, announcementRepo);
        studentService = new StudentServiceImpl(userService, courseService, assignmentRepo, submissionRepo);

        instructor = new Instructor("Dr. Smith", "drsmith", "smith@example.com", "securepwd");
        student = new Student("Jane Doe", "janed", "jane@example.com", "pass123");

        userRepo.save(instructor);
        userRepo.save(student);

        course = new Course("CS201", "Advanced Java", "OOP and more", "Core", instructor.getUserId());
        instructorService.createCourse(course);

        assignment = new Assignment("Project 1", "CS201", "Create a project", LocalDateTime.now().plusDays(3), 100);
    }

    @Test
    public void testCreateAssignmentAddsToCourseAndRepo() {
        instructorService.createAssignment(course, assignment);

        List<Assignment> courseAssignments = course.getAssignments();
        assertEquals(1, courseAssignments.size());
        assertEquals("Project 1", courseAssignments.get(0).getTitle());

        assertEquals(assignment, assignmentRepo.findById(assignment.getAssignmentId()));
    }

    @Test
    public void testCreateLessonAddsToCourse() {
        Lesson lesson = new Lesson("Intro to OOP", "OOP concepts");
        instructorService.createLesson(course, lesson);

        List<Lesson> lessons = course.getLessons();
        assertEquals(1, lessons.size());
        assertEquals("Intro to OOP", lessons.get(0).getTitle());
    }

    @Test
    public void testUploadAndDeleteMaterial() {
        Lesson lesson = new Lesson("Collections", "Java collections overview");
        Material material = new Material("Slides", "link-to-slides", "url-link", instructor, LocalDateTime.now());
        instructorService.uploadMaterial(lesson, material);

        assertEquals(1, lesson.getMaterials().size());
        assertTrue(lesson.getMaterials().contains(material));

        instructorService.deleteMaterial(lesson, material);
        assertTrue(lesson.getMaterials().isEmpty());
    }

    @Test
    public void testGradeAssignmentSuccessfully() {
        instructorService.createAssignment(course, assignment);
        studentService.enrollInCourse(student.getUserId(), "CS201");
        studentService.submitAssignment(student.getUserId(), assignment.getAssignmentId(), "Project link");

        Grade grade = new Grade(85, "Good job", LocalDateTime.now());
        instructorService.gradeAssignment(assignment, student, grade);

        Submission submission = submissionRepo.findByAssignmentId(assignment.getAssignmentId()).get(0);
        assertEquals(85, submission.getGrade().getScore());
        assertEquals("graded", submission.getStatus());
    }

    @Test
    public void testGiveFeedbackSuccessfully() {
        instructorService.createAssignment(course, assignment);
        studentService.enrollInCourse(student.getUserId(), "CS201");
        studentService.submitAssignment(student.getUserId(), assignment.getAssignmentId(), "Project link");

        instructorService.giveFeedback(assignment, student, "Well structured code");

        Submission submission = submissionRepo.findByAssignmentId(assignment.getAssignmentId()).get(0);
        assertEquals("Well structured code", submission.getInstructorRemarks());
    }

    @Test
    public void testSendAnnouncementSavesToRepoAndCourse() {
        instructorService.sendAnnouncement(course, "Exam Info", "Exam on Monday at 9AM");

        List<Announcement> announcements = course.getAnnouncements();
        assertEquals(1, announcements.size());
        assertEquals("Exam Info", announcements.get(0).getTitle());

        assertEquals(1, announcementRepo.findByCourseId(course.getCourseId()).size());
    }

    @Test
    public void testGetCoursesCreatedReturnsCorrectCourses() {
        List<Course> courses = instructorService.getCoursesCreated(instructor.getUserId());
        assertEquals(1, courses.size());
        assertEquals("CS201", courses.get(0).getCourseId());
    }

    @Test
    public void testGetAssignmentsCreatedReturnsAllAssignments() {
        instructorService.createAssignment(course, assignment);
        List<Assignment> assignments = instructorService.getAssignmentsCreated(instructor.getUserId());

        assertEquals(1, assignments.size());
        assertEquals("Project 1", assignments.get(0).getTitle());
    }

    @Test
    public void testGetLessonsCreatedReturnsAllLessons() {
        Lesson lesson1 = new Lesson("Lesson 1", "Description 1");
        Lesson lesson2 = new Lesson("Lesson 2", "Description 2");

        instructorService.createLesson(course, lesson1);
        instructorService.createLesson(course, lesson2);

        List<Lesson> lessons = instructorService.getLessonsCreated(instructor.getUserId());
        assertEquals(2, lessons.size());
    }
}
