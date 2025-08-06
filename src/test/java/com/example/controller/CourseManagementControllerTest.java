package com.example.controller;

import com.example.dto.announcement.AnnouncementResponse;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.service.CourseManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseManagementController.class)
class CourseManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseManagementService courseService;

    @Autowired
    private ObjectMapper objectMapper;

    private CourseResponse sampleCourse;

    @BeforeEach
    void setup() {
        sampleCourse = new CourseResponse();
        sampleCourse.setId(1L);
        sampleCourse.setTitle("Test Course");
        sampleCourse.setCategory("Programming");
    }

    @Test
    void createCourse_shouldReturnCreatedCourse() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Test Course");
        request.setCategory("Programming");

        Mockito.when(courseService.createCourse(any(CreateCourseRequest.class))).thenReturn(sampleCourse);

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleCourse.getId()))
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void updateCourse_shouldReturnUpdatedCourse() throws Exception {
        UpdateCourseRequest request = new UpdateCourseRequest();
        request.setTitle("Updated Course");
        request.setCategory("Math");

        CourseResponse updatedCourse = new CourseResponse();
        updatedCourse.setId(1L);
        updatedCourse.setTitle("Updated Course");
        updatedCourse.setCategory("Math");

        Mockito.when(courseService.updateCourse(eq(1L), any(UpdateCourseRequest.class))).thenReturn(updatedCourse);

        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Course"))
                .andExpect(jsonPath("$.category").value("Math"));
    }

    @Test
    void deleteCourse_shouldReturnNoContent() throws Exception {
        Mockito.doNothing().when(courseService).deleteCourse(1L);

        mockMvc.perform(delete("/api/courses/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getCourseById_shouldReturnCourse() throws Exception {
        Mockito.when(courseService.getById(1L)).thenReturn(Optional.of(sampleCourse));

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void getCourseById_notFound() throws Exception {
        Mockito.when(courseService.getById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCoursesByInstructor_shouldReturnList() throws Exception {
        Mockito.when(courseService.getAllByInstructor(10L)).thenReturn(List.of(sampleCourse));

        mockMvc.perform(get("/api/courses/instructor/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getCoursesByCategory_shouldReturnList() throws Exception {
        Mockito.when(courseService.getAllByCategory("Programming")).thenReturn(List.of(sampleCourse));

        mockMvc.perform(get("/api/courses/category/Programming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Programming"));
    }

    @Test
    void getCourseByTitle_shouldReturnCourse() throws Exception {
        Mockito.when(courseService.getByTitle("Test Course")).thenReturn(Optional.of(sampleCourse));

        mockMvc.perform(get("/api/courses/title/Test Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void getCourseByTitle_notFound() throws Exception {
        Mockito.when(courseService.getByTitle("Nonexistent")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courses/title/Nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllCourses_shouldReturnList() throws Exception {
        Mockito.when(courseService.getAll()).thenReturn(List.of(sampleCourse));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAnnouncements_shouldReturnList() throws Exception {
        AnnouncementResponse announcement = new AnnouncementResponse();
        announcement.setId(1L);
        announcement.setTitle("Announcement Title");

        Mockito.when(courseService.getAnnouncementsForCourse(1L)).thenReturn(List.of(announcement));

        mockMvc.perform(get("/api/courses/1/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Announcement Title"));
    }
}
