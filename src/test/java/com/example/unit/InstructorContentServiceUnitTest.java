package com.example.unit;

import com.example.model.*;
import com.example.repository.*;
import com.example.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InstructorContentServiceUnitTest {

    @Mock
    private InstructorRepository instructorRepo;
    @Mock
    private AssignmentService assignmentService;
    @Mock
    private LessonRepository lessonRepo;
    @Mock
    private LessonService lessonService;
    @Mock
    private AssignmentRepository assignmentRepo;
    @Mock
    private ActivityLogRepository activityLogRepo;
    @Mock
    private InstructorAuthorizationService instructorService;

    @InjectMocks
    private InstructorContentServiceImpl contentService;

    @Test
    void getLessonsCreated_shouldReturnLessons_whenInstructorExists() {
        Integer instructorId = 1;
        List<Lesson> lessons = List.of(new Lesson(10, "Lesson 1", "Content", 5, LocalDateTime.now()));

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(lessonRepo.findAllLessonsByInstructorId(instructorId)).thenReturn(lessons);

        List<Lesson> result = contentService.getLessonsCreated(instructorId);

        assertEquals(lessons, result);
        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(lessonRepo).findAllLessonsByInstructorId(instructorId);
    }

    @Test
    void getAssignmentsCreated_shouldReturnAssignments_whenInstructorExists() {
        Integer instructorId = 1;
        List<Assignment> assignments = List.of(new Assignment("Test", null, LocalDateTime.now(), 100, 5));

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(assignmentRepo.findAllAssignmentsByInstructorId(instructorId)).thenReturn(assignments);

        List<Assignment> result = contentService.getAssignmentsCreated(instructorId);

        assertEquals(assignments, result);
        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(assignmentRepo).findAllAssignmentsByInstructorId(instructorId);
    }

    @Test
    void createAssignment_shouldCallServicesAndLog_whenAllValid() {
        Integer instructorId = 1;
        Integer courseId = 5;
        Assignment assignment = new Assignment("New Assignment", null, LocalDateTime.now(), 100, courseId);
        Course course = new Course(courseId, "title", "desc", "cat", "url", instructorId);

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(instructorService.ensureAuthorizedCourseAccess(instructorId, courseId)).thenReturn(course);
        doNothing().when(assignmentService).addAssignmentToCourse(courseId, assignment);

        contentService.createAssignment(instructorId, courseId, assignment);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verify(assignmentService).addAssignmentToCourse(courseId, assignment);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Created assignment: " + assignment.getTitle())
        ));
    }


    @Test
    void createAssignment_shouldThrow_whenAssignmentIsNull() {
        Integer instructorId = 1;
        Integer courseId = 5;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);

        assertThrows(NullPointerException.class, () ->
                contentService.createAssignment(instructorId, courseId, null));

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verifyNoMoreInteractions(instructorService, assignmentService, activityLogRepo);
    }

    @Test
    void deleteAssignment_shouldCallServicesAndLog() {
        Integer instructorId = 1;
        Integer courseId = 5;
        Integer assignmentId = 10;
        Course course = new Course(courseId, "Java", "desc", "cat", "url", 1);

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(instructorService.ensureAuthorizedCourseAccess(instructorId, courseId)).thenReturn(course);
        doNothing().when(assignmentService).removeAssignmentFromCourse(courseId, assignmentId);

        contentService.deleteAssignment(instructorId, courseId, assignmentId);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verify(assignmentService).removeAssignmentFromCourse(courseId, assignmentId);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Deleted assignment ID: " + assignmentId) &&
                        log.getAction().contains(course.getTitle())
        ));
    }

    @Test
    void deleteAssignment_shouldThrow_whenAssignmentIdIsNull() {
        Integer instructorId = 1;
        Integer courseId = 5;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);

        assertThrows(NullPointerException.class, () ->
                contentService.deleteAssignment(instructorId, courseId, null));

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verifyNoMoreInteractions(instructorService, assignmentService, activityLogRepo);
    }

    @Test
    void createLesson_shouldCallServicesAndLog() {
        Integer instructorId = 1;
        Integer courseId = 5;
        Lesson lesson = new Lesson(10, "Lesson title", "content", courseId, LocalDateTime.now());
        Course course = new Course(courseId, "Java", "desc", "cat", "url", 1);

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(instructorService.ensureAuthorizedCourseAccess(instructorId, courseId)).thenReturn(course);
        doNothing().when(lessonService).addLessonToCourse(courseId, lesson);

        contentService.createLesson(instructorId, courseId, lesson);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(instructorService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verify(lessonService).addLessonToCourse(courseId, lesson);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Created lesson: " + lesson.getTitle()) &&
                        log.getAction().contains(course.getTitle())
        ));
    }

    @Test
    void createLesson_shouldThrow_whenLessonIsNull() {
        Integer instructorId = 1;
        Integer courseId = 5;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);

        assertThrows(NullPointerException.class, () ->
                contentService.createLesson(instructorId, courseId, null));

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verifyNoMoreInteractions(instructorService, lessonService, activityLogRepo);
    }

    @Test
    void deleteLesson_shouldCallServicesAndLog() {
        Integer instructorId = 1;
        Integer courseId = 5;
        Integer lessonId = 10;
        Course course = new Course(courseId, "Java", "desc", "cat", "url", 1);

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doNothing().when(lessonRepo).ensureLessonExists(lessonId);
        when(instructorService.ensureAuthorizedCourseAccess(instructorId, courseId)).thenReturn(course);
        doNothing().when(lessonService).removeLessonFromCourse(courseId, lessonId);

        contentService.deleteLesson(instructorId, courseId, lessonId);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(instructorService).ensureAuthorizedCourseAccess(instructorId, courseId);
        verify(lessonService).removeLessonFromCourse(courseId, lessonId);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Deleted lesson ID: " + lessonId) &&
                        log.getAction().contains(course.getTitle())
        ));
    }

    @Test
    void uploadMaterial_shouldCallServicesAndLog() {
        Integer instructorId = 1;
        Integer lessonId = 10;
        Material material = new Material(100, "Mat Title", "pdf", "desc", "url", 50, LocalDateTime.now());
        Lesson lesson = new Lesson(lessonId, "Lesson Title", "content", 5, LocalDateTime.now());

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doNothing().when(lessonRepo).ensureLessonExists(lessonId);
        when(instructorService.ensureAuthorizedCourseAccess(instructorId, lessonId)).thenReturn(null);
        when(lessonRepo.findById(lessonId)).thenReturn(Optional.of(lesson));
        doNothing().when(lessonService).addMaterialToLesson(lessonId, material);

        contentService.uploadMaterial(instructorId, lessonId, material);

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(instructorService).ensureAuthorizedCourseAccess(instructorId, lessonId);
        verify(lessonRepo).findById(lessonId);
        verify(lessonService).addMaterialToLesson(lessonId, material);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Uploaded material: " + material.getTitle()) &&
                        log.getAction().contains(lesson.getTitle())
        ));
    }

    @Test
    void uploadMaterial_shouldThrow_whenMaterialIsNull() {
        Integer instructorId = 1;
        Integer lessonId = 10;

        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        doNothing().when(lessonRepo).ensureLessonExists(lessonId);
        when(instructorService.ensureAuthorizedCourseAccess(instructorId, lessonId)).thenReturn(null);

        assertThrows(NullPointerException.class, () ->
                contentService.uploadMaterial(instructorId, lessonId, null));

        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(instructorService).ensureAuthorizedCourseAccess(instructorId, lessonId);
        verifyNoMoreInteractions(lessonService, activityLogRepo);
    }

    @Test
    void deleteMaterial_shouldCallServicesAndLog() {
        Integer instructorId = 1;
        Integer lessonId = 10;
        Integer materialId = 100;
        Lesson lesson = new Lesson(lessonId, "Lesson Title", "content", 5, LocalDateTime.now());

        doNothing().when(lessonRepo).ensureLessonExists(lessonId);
        doNothing().when(instructorRepo).ensureInstructorExists(instructorId);
        when(lessonRepo.findById(lessonId)).thenReturn(Optional.of(lesson));
        doNothing().when(lessonService).removeMaterialFromLesson(lessonId, materialId);

        contentService.deleteMaterial(instructorId, lessonId, materialId);

        verify(lessonRepo).ensureLessonExists(lessonId);
        verify(instructorRepo).ensureInstructorExists(instructorId);
        verify(lessonRepo).findById(lessonId);
        verify(lessonService).removeMaterialFromLesson(lessonId, materialId);
        verify(activityLogRepo).save(argThat(log ->
                log.getUserId().equals(instructorId) &&
                        log.getAction().contains("Deleted material: " + materialId) &&
                        log.getAction().contains(lesson.getTitle())
        ));
    }

}
