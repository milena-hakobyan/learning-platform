package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.dto.student.StudentResponse;
import com.example.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Test
    void registerStudent_shouldReturnCreatedStudent() throws Exception {
        StudentResponse studentResponse = new StudentResponse();
        studentResponse.setId(1L);
        studentResponse.setUsername("student1");
        studentResponse.setFirstName("John");
        studentResponse.setLastName("Doe");
        studentResponse.setEmail("john.doe@example.com");

        Mockito.when(registrationService.registerStudent(
                        anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(studentResponse);

        mockMvc.perform(post("/api/register/student")
                        .param("username", "student1")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "john.doe@example.com")
                        .param("password", "password123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("student1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void registerInstructor_shouldReturnCreatedInstructor() throws Exception {
        InstructorResponse instructorResponse = new InstructorResponse();
        instructorResponse.setId(10L);
        instructorResponse.setUsername("instructor1");
        instructorResponse.setFirstName("Jane");
        instructorResponse.setLastName("Smith");
        instructorResponse.setEmail("jane.smith@example.com");
        instructorResponse.setBio("Experienced instructor");

        Mockito.when(registrationService.registerInstructor(
                        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(instructorResponse);

        mockMvc.perform(post("/api/register/instructor")
                        .param("username", "instructor1")
                        .param("firstName", "Jane")
                        .param("lastName", "Smith")
                        .param("email", "jane.smith@example.com")
                        .param("password", "securePass")
                        .param("bio", "Experienced instructor")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.username").value("instructor1"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.bio").value("Experienced instructor"));
    }
}
