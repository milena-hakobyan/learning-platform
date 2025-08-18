package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.service.InstructorCommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/instructors/{instructorId}")
@RequiredArgsConstructor
public class InstructorCommunicationController {

    private final InstructorCommunicationService communicationService;


    @GetMapping("/announcements")
    public ResponseEntity<List<AnnouncementResponse>> getAnnouncements(@PathVariable Long instructorId) {
        List<AnnouncementResponse> announcements = communicationService.getAnnouncementsPosted(instructorId);
        return ResponseEntity.ok(announcements);
    }

    @PostMapping("/courses/{courseId}/announcements")
    public ResponseEntity<AnnouncementResponse> sendAnnouncement(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @RequestBody CreateAnnouncementRequest request) {

        AnnouncementResponse response = communicationService.sendAnnouncement(instructorId, courseId, request);

        URI location = URI.create("/api/instructors/" + instructorId
                + "/courses/" + courseId
                + "/announcements/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }
}