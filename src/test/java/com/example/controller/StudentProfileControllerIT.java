package com.example.controller;

import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.model.Role;
import com.example.model.Student;
import com.example.model.User;
import com.example.repository.JpaStudentRepository;
import com.example.repository.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class StudentProfileControllerIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaStudentRepository studentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Student student;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("student1");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("student1@example.com");
        user.setPassword("password123");
        user.setRole(Role.STUDENT);
        user.setActive(true);
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.saveAndFlush(user);

        student = new Student(user);
        student = studentRepository.saveAndFlush(student);
    }

    @Test
    void getAllStudents_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/students")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].username", is("student1")));
    }

    @Test
    void getStudentById_shouldReturnStudent_whenFound() throws Exception {
        mockMvc.perform(get("/api/students/{id}", student.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("student1")))
                .andExpect(jsonPath("$.email", is("student1@example.com")));
    }

    @Test
    void getStudentById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 9999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent_whenValidRequest() throws Exception {
        UpdateStudentRequest request = new UpdateStudentRequest();
        request.setProgressPercentage(50.0);
        request.setCompletedCourses(2);

        mockMvc.perform(put("/api/students/{studentId}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progressPercentage").value(50.0))
                .andExpect(jsonPath("$.completedCourses").value(2))
                .andExpect(jsonPath("$.currentCourses").value(0));
    }

    @Test
    void updateStudent_shouldReturnBadRequest_whenNoFieldsProvided() throws Exception {
        UpdateStudentRequest request = new UpdateStudentRequest(); // all null

        mockMvc.perform(put("/api/students/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors['AtLeastOneFieldPresent'][0]")
                        .value("At least one field must be provided for update"));

    }
}
