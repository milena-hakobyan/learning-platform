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
class InstructorGradingServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private InstructorGradingService gradingService;

    @Autowired
    private InstructorRepository instructorRepo;
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private AssignmentRepository assignmentRepo;
    @Autowired
    private SubmissionRepository submissionRepo;
    @Autowired
    private GradeRepository gradeRepo;
    @Autowired
    private ActivityLogRepository activityLogRepo;
    @Autowired
    private DatabaseConnection dbConnection;

    private Integer instructorId;
    private Integer submissionId;
    private Integer assignmentId;
    private Integer courseId;
    private Integer studentId;

    @BeforeEach
    void setup() {
        Instructor instructor = instructorRepo.save(new Instructor(null, "instruct01", "Jane", "Doe", "jane@ex.com", "pass",
                LocalDateTime.now(), true, "Expert", 5, 4.9, true));
        instructorId = instructor.getId();

        Student student = new Student(null, "johnsmith", "John", "Smith",
                "johnsmith@example.com", "password", LocalDateTime.now(), true,
                0.0, 0, 0);
        student = studentRepo.save(student);
        studentId = student.getId();

        Course course = new Course(null, "Spring Boot Testing", "Test course", "Backend",
                "http://spring.io", instructorId);


        course = courseRepo.save(course);
        courseId = course.getId();

        Assignment assignment = assignmentRepo.save(new Assignment(null, "HW1", "Basics", LocalDateTime.now().plusDays(7), 100.0, courseId));
        assignmentId = assignment.getId();

        Submission submission = submissionRepo.save(new Submission(null, studentId, assignmentId, "link", LocalDateTime.now()));
        submissionId = submission.getSubmissionId();
    }

    @Test
    void gradeSubmission_shouldUpdateStatusAndSaveGrade() {
        Grade grade = new Grade(null, 95.0 ,submissionId, "Well done", LocalDateTime.now());

        gradingService.gradeSubmission(instructorId, submissionId, grade);

        Submission updated = submissionRepo.findById(submissionId).get();
        assertEquals(SubmissionStatus.GRADED, updated.getStatus());

        Grade savedGrade = gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId).get();
        assertEquals(95.0, savedGrade.getScore());
        assertEquals("Well done", savedGrade.getFeedback());
    }

    @Test
    void getSubmissionsForAssignment_shouldReturnSubmissionsAndLogActivity() {
        List<Submission> submissions = gradingService.getSubmissionsForAssignment(instructorId, assignmentId);

        assertEquals(1, submissions.size());
        assertEquals(submissionId, submissions.get(0).getSubmissionId());

        List<ActivityLog> logs = activityLogRepo.findAllByUserId(instructorId);
        assertTrue(logs.stream().anyMatch(log -> log.getAction().contains("Viewed submissions")));
    }

    @AfterEach
    void cleanup() {
        dbConnection.execute("TRUNCATE TABLE activity_logs, grades, submissions, assignments, instructors, users, students, courses RESTART IDENTITY CASCADE");
    }
}
