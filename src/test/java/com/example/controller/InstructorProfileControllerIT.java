package com.example.controller;

import com.example.dto.instructor.InstructorResponse;
import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("local")
@AutoConfigureMockMvc
@Transactional
public class InstructorProfileControllerIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaInstructorRepository instructorRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;
    private Instructor instructor;

    @BeforeEach
    void setUp() {
        instructorRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("instructor1");
        user.setFirstName("name1");
        user.setLastName("lastName1");
        user.setPassword("password123");
        user.setEmail("instructor1@example.com");
        user.setRole(Role.INSTRUCTOR);
        user.setActive(true);
        user.setLastLogin(LocalDateTime.now());
        user = userRepository.saveAndFlush(user);

        instructor = new Instructor();
        instructor.setUser(user);
        instructor.setBio("Experienced instructor");
        instructor.setTotalCoursesCreated(5);
        instructor.setRating(4.8);
        instructor.setVerified(true);

        instructor = instructorRepository.saveAndFlush(instructor);
    }




    @Test
    void getAllInstructors_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/instructors")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].bio", is("Experienced instructor")))
                .andExpect(jsonPath("$.content[0].username", is("instructor1")));
    }

    @Test
    void getInstructorById_shouldReturnInstructor_whenFound() throws Exception {
        mockMvc.perform(get("/api/instructors/{id}", instructor.getUser().getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bio", is("Experienced instructor")))
                .andExpect(jsonPath("$.username", is("instructor1")));
    }

    @Test
    void getInstructorById_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/instructors/{id}", 9999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
