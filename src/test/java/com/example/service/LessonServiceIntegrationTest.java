package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.Lesson;
import com.example.model.Material;
import com.example.repository.CourseRepository;
import com.example.repository.InstructorRepository;
import com.example.repository.LessonRepository;
import com.example.utils.DatabaseConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

@ActiveProfiles("local")
class LessonServiceIntegrationTest extends AbstractPostgresIntegrationTest {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private DatabaseConnection dbConnection;

    private Integer courseId;
    private Integer lessonId;
    private Integer instructorId;

    @BeforeEach
    void setup() {
        Instructor instructor = new Instructor(null, "janedoe", "Jane", "Doe",
                "jane@example.com", "pass456", LocalDateTime.now(), true,
                "Expert", 5, 4.9, true);
        instructor = instructorRepository.save(instructor);

        instructorId = instructor.getId();
        // Create and save a course
        Course course = new Course(null, "Test Course", "Desc", "Category", "http://example.com", 1);
        course = courseRepository.save(course);
        courseId = course.getId();

        // Create and save a lesson
        Lesson lesson = new Lesson(null, "Test Lesson", "Lesson Content", courseId, LocalDateTime.now());
        lesson = lessonRepository.save(lesson);
        lessonId = lesson.getId();
    }

    @AfterEach
    void cleanup() {
        dbConnection.execute("TRUNCATE TABLE lesson_materials, materials, lessons, users, courses RESTART IDENTITY CASCADE");
    }

    @Test
    void getLessonsForCourse_shouldReturnLessons() {
        List<Lesson> lessons = lessonService.getLessonsForCourse(courseId);

        assertFalse(lessons.isEmpty());
        assertTrue(lessons.stream().anyMatch(lesson -> lesson.getId().equals(lessonId)));
    }

    @Test
    void addLessonToCourse_shouldAddLesson() {
        Lesson newLesson = new Lesson(null, "New Lesson", "New content", null, LocalDateTime.now());
        lessonService.addLessonToCourse(courseId, newLesson);

        List<Lesson> lessons = lessonRepository.findAllByCourseId(courseId);
        assertTrue(lessons.stream().anyMatch(l -> "New Lesson".equals(l.getTitle())));
    }

    @Test
    void removeLessonFromCourse_shouldRemoveLesson() {
        lessonService.removeLessonFromCourse(courseId, lessonId);

        assertFalse(lessonRepository.findById(lessonId).isPresent());
    }

    @Test
    void removeLessonFromCourse_shouldThrowIfLessonNotBelongsToCourse() {
        // Create a different course and lesson
        Course otherCourse = new Course(null, "Other Course", "Desc", "Category", "http://example.com", 1);
        otherCourse = courseRepository.save(otherCourse);

        Lesson otherLesson = new Lesson(null, "Other Lesson", "Other content", otherCourse.getId(), LocalDateTime.now());
        otherLesson = lessonRepository.save(otherLesson);
        Integer otherLessonId = otherLesson.getId();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            lessonService.removeLessonFromCourse(courseId, otherLessonId);
        });
        assertEquals("Lesson does not belong to the given course", ex.getMessage());
    }

    @Test
    void addMaterialToLesson_shouldAddMaterial() {
        Material material = new Material(null, "Material Title", "video", "Category", "http://material.url", 1, LocalDateTime.now());

        lessonService.addMaterialToLesson(lessonId, material);

        List<Material> materials = lessonRepository.findAllMaterialsByLessonId(lessonId);
        assertTrue(materials.stream().anyMatch(m -> "Material Title".equals(m.getTitle())));
    }

    @Test
    void removeMaterialFromLesson_shouldRemoveMaterial() {
        Material material = new Material(null, "Material to Remove", "video", "Category", "http://material.remove", 1, LocalDateTime.now());
        lessonService.addMaterialToLesson(lessonId, material);

        List<Material> materialsBefore = lessonRepository.findAllMaterialsByLessonId(lessonId);
        assertFalse(materialsBefore.isEmpty());

        Integer materialId = materialsBefore.stream()
                .filter(m -> "Material to Remove".equals(m.getTitle()))
                .findFirst()
                .get()
                .getId();

        lessonService.removeMaterialFromLesson(lessonId, materialId);

        List<Material> materialsAfter = lessonRepository.findAllMaterialsByLessonId(lessonId);
        assertFalse(materialsAfter.stream().anyMatch(m -> m.getId().equals(materialId)));
    }
}
