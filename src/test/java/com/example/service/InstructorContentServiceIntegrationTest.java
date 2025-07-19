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
class InstructorContentServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private InstructorContentService contentService;

    @Autowired
    private InstructorRepository instructorRepo;
    @Autowired
    private AssignmentRepository assignmentRepo;
    @Autowired
    private LessonRepository lessonRepo;
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private ActivityLogRepository activityLogRepo;
    @Autowired
    private DatabaseConnection db;

    private Integer instructorId;
    private Integer courseId;

    @BeforeEach
    void setup() {
        Instructor instructor = new Instructor(null, "teach01", "Alice", "Wells", "alice@uni.com", "pw",
                LocalDateTime.now(), true, "Senior", 10, 4.8, true);
        instructorId = instructorRepo.save(instructor).getId();

        Course course = new Course(null, "Intro to DB", "Basics", "CS", "http://url", instructorId);
        courseId = courseRepo.save(course).getId();
    }

    @AfterEach
    void cleanup() {
        db.execute("TRUNCATE TABLE activity_logs, materials, lesson_materials, lessons, assignments, courses, instructors, users RESTART IDENTITY CASCADE");
    }

    @Test
    void createLesson_shouldSaveLessonAndLogActivity() {
        Lesson lesson = new Lesson(null, "Lesson A", "Overview", courseId, LocalDateTime.now());

        contentService.createLesson(instructorId, courseId, lesson);

        List<Lesson> found = lessonRepo.findAllByCourseId(courseId);
        assertEquals(1, found.size());

        List<ActivityLog> logs = activityLogRepo.findAllByUserId(instructorId);
        assertTrue(logs.stream().anyMatch(log -> log.getAction().contains("Created lesson")));
    }

    @Test
    void createAssignment_shouldSaveAssignmentAndLogActivity() {
        Assignment a = new Assignment(null, "HW A", "Basic HW", LocalDateTime.now().plusDays(3), 100.0, courseId);

        contentService.createAssignment(instructorId, courseId, a);

        List<Assignment> assignments = assignmentRepo.findAllByCourseId(courseId);
        assertEquals(1, assignments.size());

        List<ActivityLog> logs = activityLogRepo.findAllByUserId(instructorId);
        assertTrue(logs.stream().anyMatch(log -> log.getAction().contains("Created assignment")));
    }

    @Test
    void deleteLesson_shouldRemoveLessonAndLogActivity() {
        Lesson lesson = lessonRepo.save(new Lesson(null, "To Remove", "X", courseId, LocalDateTime.now()));

        contentService.deleteLesson(instructorId, courseId, lesson.getId());

        assertTrue(lessonRepo.findById(lesson.getId()).isEmpty());
        assertTrue(activityLogRepo.findAllByUserId(instructorId).stream()
                .anyMatch(log -> log.getAction().contains("Deleted lesson")));
    }

    @Test
    void deleteAssignment_shouldRemoveAssignmentAndLogActivity() {
        Assignment a = assignmentRepo.save(new Assignment(null, "To Delete", "Desc", LocalDateTime.now().plusDays(1), 50.0, courseId));

        contentService.deleteAssignment(instructorId, courseId, a.getId());

        assertTrue(assignmentRepo.findById(a.getId()).isEmpty());
        assertTrue(activityLogRepo.findAllByUserId(instructorId).stream()
                .anyMatch(log -> log.getAction().contains("Deleted assignment")));
    }

    @Test
    void getLessonsCreated_shouldReturnLessonsByInstructor() {
        Lesson lesson = lessonRepo.save(new Lesson(null, "L1", "content", courseId, LocalDateTime.now()));

        List<Lesson> lessons = contentService.getLessonsCreated(instructorId);
        assertEquals(1, lessons.size());
    }

    @Test
    void getAssignmentsCreated_shouldReturnAssignmentsByInstructor() {
        assignmentRepo.save(new Assignment(null, "HW1", "Desc", LocalDateTime.now().plusDays(1), 100.0, courseId));

        List<Assignment> assignments = contentService.getAssignmentsCreated(instructorId);
        assertEquals(1, assignments.size());
    }
}