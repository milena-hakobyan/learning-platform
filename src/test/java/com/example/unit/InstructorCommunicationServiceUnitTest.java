package com.example.unit;

import com.example.model.Announcement;
import com.example.model.Course;
import com.example.repository.ActivityLogRepository;
import com.example.repository.AnnouncementRepository;
import com.example.repository.InstructorRepository;
import com.example.service.InstructorAuthorizationService;
import com.example.service.InstructorCommunicationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorCommunicationServiceUnitTest {
    @Mock
    private InstructorRepository instructorRepo;
    @Mock
    private AnnouncementRepository announcementRepo;
    @Mock
    private ActivityLogRepository activityLogRepo;
    @Mock
    private InstructorAuthorizationService instructorAuthService;

    @InjectMocks
    private InstructorCommunicationServiceImpl instructorCommunicationService;

    @Test
    void getAnnouncementsPosted() {
        Integer instructorId = 101;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);

        List<Announcement> announcements = List.of(
                new Announcement(1, "Welcome!", "Course starts soon", instructorId, 55, LocalDateTime.now()),
                new Announcement(2, "Exam Info", "Midterm exam details", instructorId, 55, LocalDateTime.now())
        );

        when(announcementRepo.findAllByInstructorId(instructorId)).thenReturn(announcements);

        List<Announcement> result = instructorCommunicationService.getAnnouncementsPosted(instructorId);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(announcementRepo).findAllByInstructorId(instructorId);
        assertEquals(announcements, result);
    }

    @Test
    void sendAnnouncement_shouldSaveAnnouncementAndLogActivity() {
        Integer instructorId = 42;
        Integer courseId = 101;
        String title = "Exam Reminder";
        String message = "Don't forget the midterm next week!";
        Course course = new Course(courseId, "Algorithms", "CS course", "CS", "http://example.com", instructorId);

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(instructorAuthService.ensureAuthorizedCourseAccess(instructorId, courseId)).thenReturn(course);

        instructorCommunicationService.sendAnnouncement(instructorId, courseId, title, message);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorAuthService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verify(announcementRepo).save(argThat(announcement ->
                announcement.getTitle().equals(title) &&
                        announcement.getContent().equals(message) &&
                        announcement.getCourseId().equals(courseId) &&
                        announcement.getPostedById().equals(instructorId)
        ));
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().equals("Sent announcement to course: Algorithms - Title: " + title)
        ));

    }

    @Test
    void sendAnnouncement_shouldThrowException_whenMessageIsNull() {
        assertThrows(NullPointerException.class, () ->
                instructorCommunicationService.sendAnnouncement(42, 101, "title", null)
        );
    }

    @Test
    void sendAnnouncement_shouldThrowException_whenTitleIsNull() {
        assertThrows(NullPointerException.class, () ->
                instructorCommunicationService.sendAnnouncement(42, 101, null, "message")
        );
    }

    @Test
    void sendAnnouncement_shouldThrowException_whenInstructorDoesNotExist() {
        Integer instructorId = 42;
        Integer courseId = 101;

        doThrow(new IllegalArgumentException("Instructor not found"))
                .when(instructorRepo).ensureInstructorExists(instructorId);

        assertThrows(IllegalArgumentException.class, () ->
                instructorCommunicationService.sendAnnouncement(instructorId, courseId, "title", "message")
        );

        verifyNoInteractions(announcementRepo);
        verifyNoInteractions(activityLogRepo);
    }

    @Test
    void sendAnnouncement_shouldThrowException_whenInstructorUnauthorized() {
        Integer instructorId = 42;
        Integer courseId = 101;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doThrow(new SecurityException("Unauthorized"))
                .when(instructorAuthService).ensureAuthorizedCourseAccess(instructorId, courseId);

        assertThrows(SecurityException.class, () ->
                instructorCommunicationService.sendAnnouncement(instructorId, courseId, "title", "message")
        );

        verify(announcementRepo, never()).save(any());
        verify(activityLogRepo, never()).save(any());
    }

}