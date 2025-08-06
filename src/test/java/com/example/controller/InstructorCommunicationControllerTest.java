package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.announcement.CreateAnnouncementRequest;
import com.example.service.InstructorCommunicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorCommunicationController.class)
public class InstructorCommunicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InstructorCommunicationService communicationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAnnouncements_shouldReturnList() throws Exception {
        AnnouncementResponse ann = new AnnouncementResponse();
        ann.setId(1L);
        ann.setTitle("Announcement 1");
        ann.setContent("Content here");

        Mockito.when(communicationService.getAnnouncementsPosted(5L)).thenReturn(List.of(ann));

        mockMvc.perform(get("/api/instructors/5/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Announcement 1"));
    }

    @Test
    void sendAnnouncement_withMatchingInstructorId_shouldCreate() throws Exception {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setInstructorId(5L);
        request.setTitle("New Announcement");
        request.setContent("Announcement content");

        AnnouncementResponse response = new AnnouncementResponse();
        response.setId(10L);
        response.setTitle("New Announcement");
        response.setContent("Announcement content");

        Mockito.when(communicationService.sendAnnouncement(any(CreateAnnouncementRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/instructors/5/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("New Announcement"));
    }

    @Test
    void sendAnnouncement_withMismatchedInstructorId_shouldReturnBadRequest() throws Exception {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setInstructorId(4L); // different from path variable 5
        request.setTitle("Invalid Announcement");
        request.setContent("Content");

        mockMvc.perform(post("/api/instructors/5/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}