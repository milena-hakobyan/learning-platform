package com.example.unit;

import com.example.model.Course;
import com.example.model.Lesson;
import com.example.model.Material;
import com.example.repository.CourseRepository;
import com.example.repository.LessonRepository;
import com.example.service.LessonService;
import com.example.service.LessonServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LessonServiceUnitTest {
    @Mock
    private CourseRepository courseRepo;
    @Mock
    private LessonRepository lessonRepo;

    @InjectMocks
    private LessonServiceImpl lessonService;

    @Test
    void getLessonsForCourse_shouldReturnLessons_whenCourseExists() {
        Integer courseId = 1;
        List<Lesson> lessons = List.of(
                new Lesson(1, "Intro", "Intro lesson", courseId, LocalDateTime.now()),
                new Lesson(2, "OOP", "OOP lesson", courseId, LocalDateTime.now())
        );

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(lessonRepo.findAllByCourseId(courseId)).thenReturn(lessons);

        List<Lesson> result = lessonService.getLessonsForCourse(courseId);

        verify(courseRepo).ensureCourseExists(courseId);
        verify(lessonRepo).findAllByCourseId(courseId);
        assertEquals(lessons, result);
    }

    @Test
    void getLessonsForCourse_shouldThrow_whenCourseDoesNotExist() {
        Integer courseId = 1;

        doThrow(new IllegalArgumentException("Course not found")).when(courseRepo).ensureCourseExists(courseId);

        assertThrows(IllegalArgumentException.class, () -> lessonService.getLessonsForCourse(courseId));

        verify(courseRepo).ensureCourseExists(courseId);
        verifyNoInteractions(lessonRepo);
    }

    @Test
    void addLessonToCourse_shouldThrow_whenLessonIsNull() {
        assertThrows(NullPointerException.class, () -> lessonService.addLessonToCourse(1, null));

        verifyNoInteractions(courseRepo);
        verifyNoInteractions(lessonRepo);
    }

    @Test
    void addLessonToCourse_shouldSave_whenValid() {
        Integer courseId = 1;
        Lesson lesson =  new Lesson(1, "Intro", "Intro lesson", courseId, LocalDateTime.now());

        doNothing().when(courseRepo).ensureCourseExists(courseId);

        lessonService.addLessonToCourse(courseId, lesson);

        assertEquals(courseId, lesson.getCourseId());
        verify(courseRepo).ensureCourseExists(courseId);
        verify(lessonRepo).save(lesson);
    }


    @Test
    void removeLessonFromCourse_shouldDelete_whenLessonBelongsToCourse() {
        Integer courseId = 1;
        Integer lessonId = 10;

        Lesson lesson = new Lesson(lessonId, "OOP", "OOP lesson", courseId, LocalDateTime.now());
        Course course = new Course(courseId, "Java", "Basics", "Programming", "url", 99);
        course.setLessons(List.of(lesson));

        when(lessonRepo.findById(lessonId)).thenReturn(Optional.of(lesson));

        lessonService.removeLessonFromCourse(courseId, lessonId);

        verify(courseRepo).ensureCourseExists(courseId);
        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(lessonRepo).findById(lessonId);
        verify(lessonRepo).delete(lessonId);
    }


    @Test
    void removeLessonFromCourse_shouldThrow_whenLessonNotInCourse() {
        Integer courseId = 1;
        Integer lessonId = 10;

        Lesson otherLesson = new Lesson(2, "OOP", "OOP lesson", courseId, LocalDateTime.now());
        Course course = new Course(courseId, "Java", "Basics", "Programming", "url", 99);
        course.setLessons(List.of(otherLesson));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                lessonService.removeLessonFromCourse(courseId, lessonId));

        assertEquals("Lesson not found", ex.getMessage());

        verify(lessonRepo, never()).delete(any());
    }

    @Test
    void addMaterialToLesson_shouldThrow_whenMaterialIsNull() {
        Integer lessonId = 1;

        assertThrows(NullPointerException.class, () ->
                lessonService.addMaterialToLesson(lessonId, null));

        verifyNoInteractions(lessonRepo);
    }

    @Test
    void addMaterialToLesson_shouldAdd_whenLessonExistsAndMaterialIsNotNull() {
        Integer lessonId = 1;
        Material material = new Material(1, "Lecture 1", "pdf","Intro to Java", "http://example.com", 100, LocalDateTime.now());

        doNothing().when(lessonRepo).ensureLessonExists(lessonId);

        lessonService.addMaterialToLesson(lessonId, material);

        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(lessonRepo).addMaterial(lessonId, material);
    }

    @Test
    void addMaterialToLesson_shouldThrow_whenLessonDoesNotExist() {
        Integer lessonId = 1;
        Material material = new Material(1, "Lecture 1", "pdf","Intro to Java", "http://example.com", 100, LocalDateTime.now());

        doThrow(new IllegalArgumentException("Lesson not found")).when(lessonRepo).ensureLessonExists(lessonId);

        assertThrows(IllegalArgumentException.class, () ->
                lessonService.addMaterialToLesson(lessonId, material));

        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(lessonRepo, never()).addMaterial(any(), any());
    }

    @Test
    void removeMaterialFromLesson_shouldRemove_whenValid() {
        Integer lessonId = 1;
        Integer materialId = 55;

        doNothing().when(lessonRepo).ensureLessonExists(lessonId);

        lessonService.removeMaterialFromLesson(lessonId, materialId);

        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(lessonRepo).removeMaterial(lessonId, materialId);
    }

    @Test
    void removeMaterialFromLesson_shouldThrow_whenMaterialIdIsNull() {
        Integer lessonId = 1;

        assertThrows(NullPointerException.class, () ->
                lessonService.removeMaterialFromLesson(lessonId, null));

        verifyNoInteractions(lessonRepo);
    }

    @Test
    void removeMaterialFromLesson_shouldThrow_whenLessonNotFound() {
        Integer lessonId = 1;
        Integer materialId = 2;

        doThrow(new IllegalArgumentException("Lesson not found"))
                .when(lessonRepo).ensureLessonExists(lessonId);

        assertThrows(IllegalArgumentException.class, () ->
                lessonService.removeMaterialFromLesson(lessonId, materialId));

        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(lessonRepo, never()).removeMaterial(any(), any());
    }
}