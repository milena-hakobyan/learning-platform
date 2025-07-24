package com.example.service;

import com.example.model.ActivityLog;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.repository.ActivityLogRepository;
import com.example.repository.CourseRepository;
import com.example.repository.InstructorRepository;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
class InstructorCourseServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private InstructorCourseService courseService;

    @Autowired
    private InstructorRepository instructorRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private ActivityLogRepository activityLogRepo;

    @Autowired
    private DatabaseConnection db;

    @AfterEach
    void cleanUp() {
        db.execute("TRUNCATE TABLE activity_logs, courses, instructors, users RESTART IDENTITY CASCADE");
    }

    @Test
    void createCourse_shouldPersistCourseAndLogActivity() {
        Instructor instructor = new Instructor(
                null, "teacherA", "Anna", "Lee", "anna@school.com", "secret",
                LocalDateTime.now(), true, "ML guru", 6, 4.8, true
        );
        instructor = instructorRepo.save(instructor);

        Course course = new Course(
                null,
                "ML 101",
                "Intro to Machine Learning",
                "AI",
                "http://ml-course.com",
                instructor.getId()
        );

        courseService.createCourse(course);

        List<Course> savedCourses = courseRepo.findAll();
        assertEquals(1, savedCourses.size());
        assertEquals("ML 101", savedCourses.get(0).getTitle());

        List<ActivityLog> logs = activityLogRepo.findAll();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getAction().contains("Created course: ML 101"));
    }

    @Test
    void deleteCourse_shouldRemoveCourseAndLogActivity() {
        Instructor instructor = instructorRepo.save(new Instructor(
                null, "delUser", "Mark", "Taylor", "mark@edu.com", "pass",
                LocalDateTime.now(), true, "SysDesign", 7, 4.6, true
        ));

        Course course = courseRepo.save(new Course(
                null,
                "Systems",
                "CS course description",
                "Computer Science",
                "http://example.com/image.jpg",
                instructor.getId()
        ));

        courseService.deleteCourse(instructor.getId(), course.getId());

        assertTrue(courseRepo.findById(course.getId()).isEmpty());

        List<ActivityLog> logs = activityLogRepo.findAll();
        assertEquals(1, logs.size());
        assertTrue(logs.get(0).getAction().contains("Deleted course: Systems"));
    }

    @Test
    void getCoursesCreated_shouldReturnInstructorCourses() {
        Instructor instructor = instructorRepo.save(new Instructor(
                null, "profjohn", "John", "Dorian", "jd@uni.com", "kelso",
                LocalDateTime.now(), true, "Medicine", 10, 4.9, true
        ));

        courseRepo.save(new Course(null, "Pathology", "Course Description", "Med", "http://image.url", instructor.getId()));
        courseRepo.save(new Course(null, "Anatomy", "Course Description", "Med", "http://image.url", instructor.getId()));

        List<Course> courses = courseService.getCoursesCreated(instructor.getId());

        assertEquals(2, courses.size());
        assertTrue(courses.stream().anyMatch(c -> c.getTitle().equals("Pathology")));
        assertTrue(courses.stream().anyMatch(c -> c.getTitle().equals("Anatomy")));
    }
}
