package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.AnnouncementMapper;
import com.example.mapper.InstructorMapper;
import com.example.model.*;
import com.example.repository.JpaActivityLogRepository;
import com.example.repository.JpaAnnouncementRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final AnnouncementMapper announcementMapper;

    public InstructorCommunicationServiceImpl(JpaInstructorRepository instructorRepo, JpaUserRepository userRepository, JpaAnnouncementRepository announcementRepo, JpaActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService, AnnouncementMapper announcementMapper) {
        this.instructorRepo = instructorRepo;
        this.userRepository = userRepository;
        this.announcementRepo = announcementRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorService = instructorService;
        this.announcementMapper = announcementMapper;
    }

    @Override
    public Page<AnnouncementResponse> getAnnouncementsPosted(Long instructorId, Pageable pageable) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with ID: " + instructorId);
        }

        return announcementRepo.findAllByInstructorId(instructorId, pageable)
                .map(announcementMapper::toDto);
    }

    @Override
    public AnnouncementResponse sendAnnouncement(CreateAnnouncementRequest dto) {
        Long instructorId = dto.getInstructorId();
        Long courseId = dto.getCourseId();

        Instructor instructor = instructorRepo.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with ID: " + instructorId));

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        Announcement announcement = announcementMapper.toEntity(dto, instructor, course);
        announcement = announcementRepo.save(announcement);

        User user = instructor.getUser();

        ActivityLog activityLog = new ActivityLog(user, "Sent announcement to course: " + course.getTitle() + " - Title: " + dto.getTitle());
        activityLogRepo.save(activityLog);

        return announcementMapper.toDto(announcement);
    }
}