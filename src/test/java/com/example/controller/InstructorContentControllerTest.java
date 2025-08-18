package com.example.controller;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.service.InstructorContentService;
import com.example.service.InstructorCourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstructorContentController.class)
public class InstructorContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorContentService contentService;

    @MockBean
    private InstructorCourseService courseService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createAssignment_shouldReturnCreatedAssignment() throws Exception {
        CreateAssignmentRequest request = new CreateAssignmentRequest();
        request.setTitle("New Assignment");
        request.setDescription("Desc");

        AssignmentResponse response = new AssignmentResponse();
        response.setId(5L);
        response.setTitle(request.getTitle());

        Mockito.when(contentService.createAssignment(eq(1L), eq(100L), any(CreateAssignmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/instructors/1/courses/100/assignments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.title").value("New Assignment"));
    }

    @Test
    void deleteAssignment_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/instructors/1/courses/100/assignments/5"))
                .andExpect(status().isNoContent());

        Mockito.verify(contentService).deleteAssignment(1L, 100L, 5L);
    }

    @Test
    void createLesson_shouldReturnCreatedLesson() throws Exception {
        CreateLessonRequest request = new CreateLessonRequest();
        request.setTitle("New Lesson");

        LessonResponse response = new LessonResponse();
        response.setId(10L);
        response.setTitle(request.getTitle());

        Mockito.when(contentService.createLesson(eq(1L), eq(100L), any(CreateLessonRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/instructors/1/courses/100/lessons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.title").value("New Lesson"));
    }

    @Test
    void deleteLesson_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/instructors/1/courses/100/lessons/10"))
                .andExpect(status().isNoContent());

        Mockito.verify(contentService).deleteLesson(1L, 100L, 10L);
    }

    @Test
    void addMaterialToLesson_shouldReturnCreatedMaterial() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest();
        request.setTitle("Material Title");
        request.setUrl("http://example.com");

        MaterialResponse response = new MaterialResponse();
        response.setId(55L);
        response.setTitle(request.getTitle());

        Mockito.when(contentService.addMaterialToLesson(eq(1L), eq(10L), any(CreateMaterialRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/instructors/1/courses/100/lessons/10/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$.title").value("Material Title"));
    }

    @Test
    void deleteMaterialFromLesson_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/instructors/1/courses/100/lessons/10/materials/55"))
                .andExpect(status().isNoContent());

        Mockito.verify(contentService).deleteMaterialFromLesson(1L, 10L, 55L);
    }

    @Test
    void addMaterialToAssignment_shouldReturnCreatedMaterial() throws Exception {
        CreateMaterialRequest request = new CreateMaterialRequest();
        request.setTitle("Assignment Material");
        request.setUrl("http://example.com/assignment");

        MaterialResponse response = new MaterialResponse();
        response.setId(66L);
        response.setTitle(request.getTitle());

        Mockito.when(contentService.addMaterialToAssignment(eq(1L), eq(20L), any(CreateMaterialRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/instructors/1/courses/100/assignments/20/materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(66L))
                .andExpect(jsonPath("$.title").value("Assignment Material"));
    }

    @Test
    void deleteMaterialFromAssignment_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/instructors/1/courses/100/assignments/20/materials/66"))
                .andExpect(status().isNoContent());

        Mockito.verify(contentService).deleteMaterialFromAssignment(1L, 20L, 66L);
    }

    @Test
    void createCourse_shouldReturnCreatedCourse() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("New Course");

        CourseResponse response = new CourseResponse();
        response.setId(101L);
        response.setTitle(request.getTitle());

        Mockito.when(courseService.createCourse(any(CreateCourseRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/instructors/1/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.title").value("New Course"));
    }

    @Test
    void deleteCourse_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/instructors/1/courses/101"))
                .andExpect(status().isNoContent());

        Mockito.verify(courseService).deleteCourse(1L, 101L);
    }

    @Test
    void getCoursesCreated_shouldReturnList() throws Exception {
        CourseResponse course = new CourseResponse();
        course.setId(101L);
        course.setTitle("Existing Course");

        Mockito.when(courseService.getCoursesCreated(1L))
                .thenReturn(List.of(course));

        mockMvc.perform(get("/api/instructors/1/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(101L))
                .andExpect(jsonPath("$[0].title").value("Existing Course"));
    }
}
