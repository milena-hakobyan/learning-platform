package com.example.service;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.model.Announcement;
import java.util.List;



public interface InstructorCommunicationService {
    List<AnnouncementResponse> getAnnouncementsPosted(Long instructorId);

    AnnouncementResponse sendAnnouncement(Long courseId, Long instructorId, CreateAnnouncementRequest request);
}
