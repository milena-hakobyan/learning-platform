package com.example.service;

import com.example.model.ActivityLog;
import com.example.model.Announcement;
import com.example.model.Course;
import com.example.repository.ActivityLogRepository;
import com.example.repository.AnnouncementRepository;
import com.example.repository.InstructorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorCommunicationServiceImpl implements InstructorCommunicationService {
    private final InstructorRepository instructorRepo;
    private final AnnouncementRepository announcementRepo;
    private final ActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorService;

    public InstructorCommunicationServiceImpl(InstructorRepository instructorRepo, AnnouncementRepository announcementRepo, ActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService) {
        this.instructorRepo = instructorRepo;
        this.announcementRepo = announcementRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorService = instructorService;
    }

    @Override
    public List<Announcement> getAnnouncementsPosted(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return announcementRepo.findAllByInstructorId(instructorId);
    }


    @Override
    public void sendAnnouncement(Integer instructorId, Integer courseId, String title, String message) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        Announcement announcement = new Announcement(null, title, message, instructorId, courseId);
        announcementRepo.save(announcement);
        activityLogRepo.save(new ActivityLog(instructorId, "Sent announcement to course: " + course.getTitle() + " - Title: " + title));
    }
}
