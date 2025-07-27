package com.example.service;

import com.example.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorCommunicationServiceImpl implements InstructorCommunicationService {
    private final InstructorRepository instructorRepo;
    private final UserRepository userRepository;
    private final AnnouncementRepository announcementRepo;
    private final ActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorService;

    public InstructorCommunicationServiceImpl(InstructorRepository instructorRepo, UserRepository userRepository, AnnouncementRepository announcementRepo, ActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService) {
        this.instructorRepo = instructorRepo;
        this.userRepository = userRepository;
        this.announcementRepo = announcementRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorService = instructorService;
    }

    @Override
    public List<Announcement> getAnnouncementsPosted(Long instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return announcementRepo.findAllByInstructorId(instructorId);
    }

    @Override
    public void sendAnnouncement(Long instructorId, Long courseId, String title, String message) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);
        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found"));

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Announcement announcement = new Announcement(null, title, message, instructor, course);
        announcementRepo.save(announcement);

        ActivityLog activityLog = new ActivityLog(user, "Sent announcement to course: " + course.getTitle() + " - Title: " + title);
        activityLogRepo.save(activityLog);
    }
}