package com.example.service;

import com.example.model.Announcement;
import java.util.List;



public interface InstructorCommunicationService {
    List<Announcement> getAnnouncementsPosted(Integer instructorId);

    void sendAnnouncement(Integer instructorId, Integer courseId, String title, String message);
}
