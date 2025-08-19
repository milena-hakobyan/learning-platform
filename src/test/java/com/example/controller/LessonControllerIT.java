package com.example.controller;

import com.example.model.*;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaLessonRepository;
import com.example.repository.JpaUserRepository;
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
class LessonControllerIT extends AbstractPostgresIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaUserRepository userRepo;

    @Autowired
    private JpaInstructorRepository instructorRepo;

    @Autowired
    private JpaCourseRepository courseRepo;

    @Autowired
    private JpaLessonRepository lessonRepo;

    private Course course;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        lessonRepo.deleteAll();
        courseRepo.deleteAll();
        instructorRepo.deleteAll();
        userRepo.deleteAll();

        User user = new User();
        user.setUsername("username");
        user.setEmail("instructor@example.com");
        user.setPassword("password");
        user.setRole(Role.INSTRUCTOR);
        user = userRepo.save(user);

        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setBio("Test instructor");
        instructor = instructorRepo.save(instructor);

        course = new Course();
        course.setTitle("Spring Boot Course");
        course.setDescription("Learn Spring Boot");
        course.setInstructor(instructor);
        course = courseRepo.save(course);

        lesson = new Lesson();
        lesson.setTitle("Lesson 1");
        lesson.setContent("Introduction to Spring");
        lesson.setUploadedAt(LocalDateTime.now());
        lesson.setCourse(course);
        lesson = lessonRepo.save(lesson);
    }

    @Test
    void getLessonsForCourse_shouldReturnPagedList() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/lessons", course.getId())
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is("Lesson 1")));
    }

    @Test
    void getLessonDetails_shouldReturnLesson() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/lessons/{lessonId}", course.getId(), lesson.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(lesson.getId().intValue())))
                .andExpect(jsonPath("$.title", is("Lesson 1")))
                .andExpect(jsonPath("$.courseId", is(course.getId().intValue())));
    }

    @Test
    void getLessonsForCourse_withInvalidCourseId_shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/lessons", 9999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    void getLessonDetails_withInvalidLessonId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/courses/{courseId}/lessons/{lessonId}", course.getId(), 9999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail", containsString("Lesson not found")));
    }
}
