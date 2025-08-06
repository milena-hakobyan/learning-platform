package com.example.controller;

import com.example.dto.student.StudentResponse;
import com.example.service.CourseEnrollmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseEnrollmentController.class)
class CourseEnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseEnrollmentService enrollmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private StudentResponse studentResponse;

    @BeforeEach
    void setUp() {
        studentResponse = new StudentResponse();
        studentResponse.setId(1L);
        studentResponse.setFirstName("Alice");
        studentResponse.setLastName("Smith");
        studentResponse.setEmail("alice@example.com");
    }

    @Test
    void getEnrolledStudents_shouldReturnListOfStudents() throws Exception {
        when(enrollmentService.getEnrolledStudents(10L)).thenReturn(List.of(studentResponse));

        mockMvc.perform(get("/api/enrollments/10/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    void enrollStudent_shouldReturnNoContent() throws Exception {
        doNothing().when(enrollmentService).enrollStudent(10L, 1L);

        mockMvc.perform(post("/api/enrollments/10/students/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void unenrollStudent_shouldReturnNoContent() throws Exception {
        doNothing().when(enrollmentService).unenrollStudent(10L, 1L);

        mockMvc.perform(delete("/api/enrollments/10/students/1"))
                .andExpect(status().isNoContent());
    }
}
