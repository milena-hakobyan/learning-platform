package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.model.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;



public interface InstructorCommunicationService {
    Page<AnnouncementResponse> getAnnouncementsPosted(Long instructorId, Pageable pageable);

    AnnouncementResponse sendAnnouncement(CreateAnnouncementRequest request);
}
