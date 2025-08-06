package com.example.service;

import com.example.model.Announcement;
import java.util.List;



public interface InstructorCommunicationService {
    List<Announcement> getAnnouncementsPosted(Long instructorId);

    void sendAnnouncement(Long instructorId, Long courseId, String title, String message);
}
