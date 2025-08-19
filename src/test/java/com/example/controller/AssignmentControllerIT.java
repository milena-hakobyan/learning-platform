package com.example.controller;

import com.example.dto.assignment.AssignmentResponse;
import com.example.model.Assignment;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JpaAssignmentRepository;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class AssignmentControllerIT extends AbstractPostgresIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepo;

    @Autowired
    private JpaInstructorRepository instructorRepo;

    @Autowired
    private JpaCourseRepository courseRepo;

    @Autowired
    private JpaAssignmentRepository assignmentRepo;

    private Course savedCourse;
    private Assignment savedAssignment;

    @BeforeEach
    void setUp() {
        assignmentRepo.deleteAll();
        courseRepo.deleteAll();
        instructorRepo.deleteAll();
        userRepo.deleteAll();

        User user = new User();
        user.setUsername("instructor1");
        user.setEmail("instructor1@example.com");
        user.setPassword("secret");
        user.setRole(Role.INSTRUCTOR);
        user = userRepo.save(user);

        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setBio("Experienced instructor");
        instructor = instructorRepo.save(instructor);

        Course course = new Course();
        course.setTitle("Spring Boot Course");
        course.setDescription("Learn Spring Boot deeply");
        course.setCategory("Programming");
        course.setInstructor(instructor);
        savedCourse = courseRepo.save(course);

        Assignment assignment = new Assignment();
        assignment.setTitle("First Assignment");
        assignment.setDescription("Solve exercises 1-10");
        assignment.setDueDate(LocalDateTime.now().plusDays(7));
        assignment.setMaxScore(100);
        assignment.setCourse(savedCourse);
        savedAssignment = assignmentRepo.save(assignment);
    }

    @Test
    void getAssignmentsByCourse_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/assignments", savedCourse.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is("First Assignment")));
    }

    @Test
    void getAssignmentDetails_shouldReturnAssignment() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/assignments/{assignmentId}",
                        savedCourse.getId(), savedAssignment.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedAssignment.getId().intValue())))
                .andExpect(jsonPath("$.title", is("First Assignment")))
                .andExpect(jsonPath("$.courseId", is(savedCourse.getId().intValue())));
    }

    @Test
    void getAssignmentDetails_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/assignments/{assignmentId}",
                        savedCourse.getId(), 9999L) // non-existent assignmentId
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource not found")))
                .andExpect(jsonPath("$.detail", containsString("Assignment")))
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void getAssignmentsByCourse_withInvalidCourse_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/assignments", 9999L) // non-existent courseId
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource not found")))
                .andExpect(jsonPath("$.detail", containsString("Course")))
                .andExpect(jsonPath("$.status", is(404)));
    }

}
