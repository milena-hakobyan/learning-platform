package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.service.InstructorCommunicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}/announcements")
public class InstructorCommunicationController {

    private final InstructorCommunicationService communicationService;

    public InstructorCommunicationController(InstructorCommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(@PathVariable Long instructorId) {
        List<AnnouncementResponse> announcements = communicationService.getAnnouncementsPosted(instructorId);
        return ResponseEntity.ok(announcements);
    }

    @PostMapping
    public ResponseEntity<AnnouncementResponse> sendAnnouncement(@PathVariable Long instructorId,
                                                                 @RequestBody CreateAnnouncementRequest request) {
        if (!instructorId.equals(request.getInstructorId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AnnouncementResponse response = communicationService.sendAnnouncement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
