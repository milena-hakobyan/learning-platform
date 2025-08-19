package com.example.controller;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.Role;
import com.example.model.User;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaUserRepository;
import com.example.specification.SearchCriteria;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class CourseManagementControllerIT extends AbstractPostgresIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepo;

    @Autowired
    private JpaInstructorRepository instructorRepo;

    @Autowired
    private JpaCourseRepository courseRepo;

    @Autowired
    private ObjectMapper objectMapper;

    private Course course;

    @BeforeEach
    void setUp() {
        // Clean repositories
        courseRepo.deleteAll();
        instructorRepo.deleteAll();
        userRepo.deleteAll();

        // Create user
        User user = new User();
        user.setUsername("username");
        user.setEmail("instructor@example.com");
        user.setPassword("password");
        user.setRole(Role.INSTRUCTOR);
        user = userRepo.save(user);

        // Create instructor
        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setBio("Test instructor");
        instructor = instructorRepo.save(instructor);

        // Create course
        course = new Course();
        course.setTitle("Spring Boot Course");
        course.setDescription("Learn Spring Boot");
        course.setInstructor(instructor);
        course.setCategory("Programming");
        course.setUrl("https://example.com/new-course");
        course = courseRepo.save(course);
    }

    @Test
    void getAllCourses_shouldReturnCourses() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is("Spring Boot Course")));
    }

    @Test
    void getCourseById_shouldReturnCourse() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}", course.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(course.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Spring Boot Course")));
    }

    @Test
    void getCourseById_withInvalidId_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}", 9999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource not found")))
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void createCourse_shouldReturnCreatedCourse() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("New Course");
        request.setDescription("New Description");
        request.setInstructorId(course.getInstructor().getId());
        request.setCategory("Programming");
        request.setUrl("https://example.com/new-course");

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/courses/")))
                .andExpect(jsonPath("$.title", is("New Course")));
    }

    @Test
    void createCourse_withDuplicateTitle_shouldReturnBadRequest() throws Exception {
        CreateCourseRequest request = new CreateCourseRequest();
        request.setTitle("Spring Boot Course");
        request.setDescription("Duplicate Description");
        request.setInstructorId(course.getInstructor().getId());

        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Validation Failed")))
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void updateCourse_shouldReturnUpdatedCourse() throws Exception {
        UpdateCourseRequest request = new UpdateCourseRequest();
        request.setTitle("Updated Course Title");
        request.setDescription("Updated Description");

        mockMvc.perform(put("/api/courses/{courseId}", course.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Course Title")));
    }

    @Test
    void deleteCourse_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/courses/{courseId}", course.getId()))
                .andExpect(status().isNoContent());
    }


    @Test
    void getCoursesByTitle_shouldReturnFilteredCourses() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("title", "Spring")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", containsString("Spring")));
    }

    @Test
    void getCoursesByCategory_shouldReturnFilteredCourses() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("category", "Programming")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].category", is("Programming")));
    }

    @Test
    void getCoursesByCreatedAt_shouldReturnFilteredCourses() throws Exception {
        String after = course.getCreatedAt().minusDays(1).toString();
        String before = course.getCreatedAt().plusDays(1).toString();

        mockMvc.perform(get("/api/courses")
                        .param("createdAfter", after)
                        .param("createdBefore", before)
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void getCoursesByTitleAndCategory_shouldReturnFilteredCourses() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .param("title", "Spring")
                        .param("category", "Programming")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", containsString("Spring")))
                .andExpect(jsonPath("$.content[0].category", is("Programming")));
    }

}