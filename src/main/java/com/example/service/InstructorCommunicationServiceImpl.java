package com.example.service;

import com.example.model.*;
import com.example.repository.JpaActivityLogRepository;
import com.example.repository.JpaAnnouncementRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorCommunicationServiceImpl implements InstructorCommunicationService {
    private final JpaInstructorRepository instructorRepo;
    private final JpaUserRepository userRepository;
    private final JpaAnnouncementRepository announcementRepo;
    private final JpaActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorService;

    public InstructorCommunicationServiceImpl(JpaInstructorRepository instructorRepo, JpaUserRepository userRepository, JpaAnnouncementRepository announcementRepo, JpaActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService) {
        this.instructorRepo = instructorRepo;
        this.userRepository = userRepository;
        this.announcementRepo = announcementRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorService = instructorService;
    }

    @Override
    public List<Announcement> getAnnouncementsPosted(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }

        return announcementRepo.findAllByInstructorId(instructorId);
    }

    @Override
    public void sendAnnouncement(Long instructorId, Long courseId, String title, String message) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found with ID: " + instructorId));

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + instructorId));

        Announcement announcement = new Announcement(null, title, message, instructor, course);
        announcementRepo.save(announcement);

        ActivityLog activityLog = new ActivityLog(user, "Sent announcement to course: " + course.getTitle() + " - Title: " + title);
        activityLogRepo.save(activityLog);
    }

}