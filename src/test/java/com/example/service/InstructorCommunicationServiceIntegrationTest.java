package com.example.service;

import com.example.model.Announcement;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.ActivityLog;
import com.example.repository.AnnouncementRepository;
import com.example.repository.CourseRepository;
import com.example.repository.InstructorRepository;
import com.example.repository.ActivityLogRepository;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class InstructorCommunicationServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private InstructorCommunicationService communicationService;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer instructorId;
    private Integer courseId;

    @BeforeEach
    void setup() {
        Instructor instructor = new Instructor(
                null, "alexsmith", "Alex", "Smith",
                "alex@example.com", "pass123", LocalDateTime.now(),
                true, "Java expert", 5, 4.9, true
        );
        instructor = instructorRepository.save(instructor);
        instructorId = instructor.getId();

        Course course = new Course(
                null, "Java 101", "Basic Java course",
                "Backend", "http://java.com", instructorId
        );
        course = courseRepository.save(course);
        courseId = course.getId();
    }

    @Test
    void getAnnouncementsPosted_shouldReturnList() {
        communicationService.sendAnnouncement(instructorId, courseId, "Update", "Assignment due tomorrow");

        List<Announcement> list = communicationService.getAnnouncementsPosted(instructorId);

        assertEquals(1, list.size());
        assertEquals("Update", list.get(0).getTitle());
    }

    @Test
    void sendAnnouncement_shouldCreateAnnouncementAndLog() {
        communicationService.sendAnnouncement(instructorId, courseId, "Exam Info", "Midterm on Monday");

        List<Announcement> announcements = announcementRepository.findAllByInstructorId(instructorId);
        assertEquals(1, announcements.size());
        assertEquals("Exam Info", announcements.get(0).getTitle());

        List<ActivityLog> logs = activityLogRepository.findAllByUserId(instructorId);
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getAction().contains("Sent announcement"));
    }

    @Test
    void sendAnnouncement_shouldThrow_whenTitleIsNull() {
        assertThrows(NullPointerException.class, () ->
                communicationService.sendAnnouncement(instructorId, courseId, null, "msg")
        );
    }

    @Test
    void sendAnnouncement_shouldThrow_whenMessageIsNull() {
        assertThrows(NullPointerException.class, () ->
                communicationService.sendAnnouncement(instructorId, courseId, "title", null)
        );
    }

    @Test
    void sendAnnouncement_shouldThrow_whenInstructorDoesNotExist() {
        Integer fakeId = 9999;
        assertThrows(RuntimeException.class, () ->
                communicationService.sendAnnouncement(fakeId, courseId, "title", "msg")
        );
    }

    @AfterEach
    void cleanUp() {
        dbConnection.execute("TRUNCATE TABLE activity_logs, announcements, courses, instructors, users RESTART IDENTITY CASCADE");
    }
}
