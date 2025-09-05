package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.exception.ResourceNotFoundException;
import com.example.feign.UserServiceClient;
import com.example.mapper.AnnouncementMapper;
import com.example.model.*;
import com.example.repository.JpaAnnouncementRepository;
import com.example.repository.JpaInstructorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InstructorCommunicationServiceImpl implements InstructorCommunicationService {
    private final JpaInstructorRepository instructorRepo;
    private final JpaAnnouncementRepository announcementRepo;
    private final InstructorAuthorizationService instructorService;
    private final AnnouncementMapper announcementMapper;
    private final UserServiceClient userClient;

    public InstructorCommunicationServiceImpl(JpaInstructorRepository instructorRepo, JpaAnnouncementRepository announcementRepo, InstructorAuthorizationService instructorService, AnnouncementMapper announcementMapper, UserServiceClient userClient) {
        this.instructorRepo = instructorRepo;
        this.announcementRepo = announcementRepo;
        this.instructorService = instructorService;
        this.announcementMapper = announcementMapper;
        this.userClient = userClient;
    }

    @Override
    public Page<AnnouncementResponse> getAnnouncementsPosted(Long instructorId, Pageable pageable) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found with ID: " + instructorId);
        }

        return announcementRepo.findAllByInstructorUserId(instructorId, pageable)
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

        userClient.logActivity(
                instructorId,
                new UserServiceClient.ActivityLogRequest(
                        "Sent announcement to course: " + course.getTitle() + " - Title: " + dto.getTitle()
                )
        );

        return announcementMapper.toDto(announcement);
    }
}