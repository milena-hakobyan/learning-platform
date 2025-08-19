package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.service.InstructorCommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<AnnouncementResponse>> getAnnouncements(
            @PathVariable Long instructorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(communicationService.getAnnouncementsPosted(instructorId, pageable));
    }
    @PostMapping("/courses/{courseId}/announcements")
    public ResponseEntity<AnnouncementResponse> sendAnnouncement(
            @PathVariable Long instructorId,
            @PathVariable Long courseId,
            @Valid @RequestBody CreateAnnouncementRequest request) {

        AnnouncementResponse response = communicationService.sendAnnouncement(request);

        URI location = URI.create("/api/instructors/" + instructorId
                + "/courses/" + courseId
                + "/announcements/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }
}