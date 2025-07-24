package com.example.unit;

import static org.junit.jupiter.api.Assertions.*;

import com.example.model.*;
import com.example.repository.*;
import com.example.service.CourseManagementService;
import com.example.service.CourseManagementServiceImpl;
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

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseManagementServiceUnitTest {

    @Mock
    private CourseRepository courseRepo;
    @Mock
    private LessonRepository lessonRepo;
    @Mock
    private AssignmentRepository assignmentRepo;
    @Mock
    private SubmissionRepository submissionRepo;
    @Mock
    private AnnouncementRepository announcementRepo;

    @InjectMocks
    private CourseManagementServiceImpl courseService;

    @Test
    void createCourse_shouldThrow_whenCourseIsNull() {
        assertThrows(NullPointerException.class, () -> courseService.createCourse(null));

        verifyNoInteractions(courseRepo);
    }

    @Test
    void createCourse_shouldThrow_whenCourseAlreadyExists() {
        Course course = new Course(1, "Java", "Desc", "Prog", "url", 1);

        when(courseRepo.findByTitle(course.getTitle())).thenReturn(Optional.of(course));

        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(course));
    }

    @Test
    void createCourse_shouldSave_whenValid() {
        Course course = new Course(1, "Java", "Desc", "Prog", "url", 1);

        when(courseRepo.findByTitle(course.getTitle())).thenReturn(Optional.empty());

        courseService.createCourse(course);

        verify(courseRepo).save(course);
    }

    @Test
    void updateCourse_shouldThrow_whenNull() {
        assertThrows(NullPointerException.class, () -> courseService.updateCourse(null));

        verifyNoInteractions(courseRepo);
    }

    @Test
    void updateCourse_shouldCallSave() {
        Course course = new Course(1, "Java", "Desc", "Prog", "url", 1);

        doNothing().when(courseRepo).ensureCourseExists(course.getId());

        courseService.updateCourse(course);

        verify(courseRepo).update(course);
    }

    @Test
    void deleteCourse_shouldDeleteSubmissionsAndAssignmentsAndCourse() {
        Integer courseId = 1;
        Assignment assignment = new Assignment("A1", null, LocalDateTime.now(), 100, courseId);
        assignment.setId(101);
        Submission submission = new Submission(1, 101, "link", LocalDateTime.now());
        submission.setSubmissionId(201);

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(assignmentRepo.findAllByCourseId(courseId)).thenReturn(List.of(assignment));
        when(submissionRepo.findAllByAssignmentId(assignment.getId())).thenReturn(List.of(submission));

        courseService.deleteCourse(courseId);

        verify(submissionRepo).delete(submission.getSubmissionId());
        verify(assignmentRepo).delete(assignment.getId());
        verify(courseRepo).delete(courseId);
    }

    @Test
    void getAnnouncementsForCourse_shouldReturnList() {
        Integer courseId = 1;
        List<Announcement> announcements = List.of(new Announcement(1, "T", "C", 1, courseId, LocalDateTime.now()));

        doNothing().when(courseRepo).ensureCourseExists(courseId);
        when(announcementRepo.findAllByCourseId(courseId)).thenReturn(announcements);

        List<Announcement> result = courseService.getAnnouncementsForCourse(courseId);

        assertEquals(announcements, result);
    }

    @Test
    void getCourseById_shouldThrow_whenNull() {
        assertThrows(NullPointerException.class, () -> courseService.getCourseById(null));

        verifyNoInteractions(announcementRepo);
    }

    @Test
    void getByIdWithLessons_shouldReturnCourseWithLessons() {
        Integer courseId = 1;
        Course course = new Course(courseId, "T", "D", "C", "url", 1);
        course.setLessons(List.of());

        when(courseRepo.findById(courseId)).thenReturn(Optional.of(course));
        when(lessonRepo.findAllByCourseId(courseId)).thenReturn(List.of(
                new Lesson(1, "L1", "content", courseId, LocalDateTime.now())
        ));

        Optional<Course> result = courseService.getByIdWithLessons(courseId);
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getLessons().size());
    }

    @Test
    void getCoursesByInstructor_shouldThrow_whenNull() {
        assertThrows(NullPointerException.class, () -> courseService.getCoursesByInstructor(null));
    }

    @Test
    void getCoursesByCategory_shouldThrow_whenNull() {
        assertThrows(NullPointerException.class, () -> courseService.getCoursesByCategory(null));
    }

    @Test
    void getCourseByTitle_shouldThrow_whenNull() {
        assertThrows(NullPointerException.class, () -> courseService.getCourseByTitle(null));
    }

    @Test
    void getAllCourses_shouldReturnList() {
        List<Course> courses = List.of(new Course(1, "T", "D", "C", "url", 1));
        when(courseRepo.findAll()).thenReturn(courses);

        assertEquals(courses, courseService.getAllCourses());
    }
}