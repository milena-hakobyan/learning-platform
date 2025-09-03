package com.example.controller;

import com.example.model.*;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaStudentRepository;
import com.example.repository.JpaUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
public class CourseEnrollmentControllerIT extends AbstractPostgresIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepository;

    @Autowired
    private JpaStudentRepository studentRepository;

    @Autowired
    private JpaInstructorRepository instructorRepository;

    @Autowired
    private JpaCourseRepository courseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User instructorUser;
    private Instructor instructor;
    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        courseRepository.deleteAll();
        userRepository.deleteAll();

        instructorUser = new User();
        instructorUser.setUsername("instructor1");
        instructorUser.setEmail("instructor1@example.com");
        instructorUser.setPassword("password123");
        instructorUser.setRole(Role.INSTRUCTOR);
        instructorUser.setActive(true);
        instructorUser.setLastLogin(LocalDateTime.now());
        userRepository.saveAndFlush(instructorUser);

        instructor = new Instructor();
        instructor.setUser(instructorUser);
        instructorRepository.saveAndFlush(instructor);

        User studentUser = new User();
        studentUser.setUsername("student1");
        studentUser.setEmail("student1@example.com");
        studentUser.setPassword("password123");
        studentUser.setRole(Role.STUDENT);
        studentUser.setActive(true);
        studentUser.setLastLogin(LocalDateTime.now());
        userRepository.saveAndFlush(studentUser);

        student = new Student();
        student.setUser(studentUser);
        studentRepository.saveAndFlush(student);

        course = new Course();
        course.setTitle("Test Course");
        course.setDescription("Test course description");
        course.setInstructor(instructor);
        courseRepository.saveAndFlush(course);
    }

    @Test
    void getEnrolledStudents_shouldReturnEmptyPageInitially() throws Exception {
        mockMvc.perform(get("/api/courses/" + course.getId() + "/students")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void enrollStudent_shouldEnrollAndReturnNoContent() throws Exception {
        mockMvc.perform(post("/api/courses/" + course.getId() + "/students/" + student.getUser().getId()))
                .andExpect(status().isNoContent());

        // After enrollment
        mockMvc.perform(get("/api/courses/" + course.getId() + "/students")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].username").value("student1"));
    }

    @Test
    void unenrollStudent_shouldRemoveEnrollmentAndReturnNoContent() throws Exception {
        // Enroll first
        mockMvc.perform(post("/api/courses/" + course.getId() + "/students/" + student.getUser().getId()))
                .andExpect(status().isNoContent());

        // Then unenroll
        mockMvc.perform(delete("/api/courses/" + course.getId() + "/students/" + student.getUser().getId()))
                .andExpect(status().isNoContent());

        // Verify empty
        mockMvc.perform(get("/api/courses/" + course.getId() + "/students")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void enrollStudent_shouldReturnNotFound_whenStudentDoesNotExist() throws Exception {
        Long nonExistentStudentId = 99999L;

        mockMvc.perform(post("/api/courses/" + course.getId() + "/students/" + nonExistentStudentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void enrollStudent_shouldReturnNotFound_whenCourseDoesNotExist() throws Exception {
        Long nonExistentCourseId = 99999L;

        mockMvc.perform(post("/api/courses/" + nonExistentCourseId + "/students/" + student.getUser().getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    void unenrollStudent_shouldReturnNotFound_whenStudentOrCourseDoesNotExist() throws Exception {
        Long nonExistentStudentId = 99999L;
        Long nonExistentCourseId = 99999L;

        mockMvc.perform(delete("/api/courses/" + course.getId() + "/students/" + nonExistentStudentId))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/courses/" + nonExistentCourseId + "/students/" + student.getUser().getId()))
                .andExpect(status().isNotFound());
    }
}
