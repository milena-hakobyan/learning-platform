package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseManagementServiceImpl implements CourseManagementService {

    private final JpaCourseRepository courseRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaAnnouncementRepository announcementRepo;

    public CourseManagementServiceImpl(
            JpaCourseRepository courseRepo,
            JpaLessonRepository lessonRepo,
            JpaAssignmentRepository assignmentRepo,
            JpaSubmissionRepository submissionRepo,
            JpaAnnouncementRepository announcementRepo
    ) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.announcementRepo = announcementRepo;
    }

    @Override
    public void createCourse(Course course) {
        if (course == null) throw new IllegalArgumentException("Course cannot be null");
        if (course.getTitle() == null) throw new IllegalArgumentException("Course title cannot be null");

        if (courseRepo.findByTitle(course.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Course with title '" + course.getTitle() + "' already exists.");
        }
        courseRepo.save(course);
    }

    @Override
    public void updateCourse(Course course) {
        if (course == null) throw new IllegalArgumentException("Course cannot be null");
        if (course.getId() == null) throw new IllegalArgumentException("Course ID cannot be null");

        if (!courseRepo.existsById(course.getId())) {
            throw new IllegalArgumentException("Course not found with ID: " + course.getId());
        }
        courseRepo.save(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");

        if (!courseRepo.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        assignmentRepo.findAllByCourseId(courseId)
                .forEach(assignment -> {
                    submissionRepo.findAllByAssignmentId(assignment.getId())
                            .forEach(submission -> submissionRepo.deleteById(submission.getId()));
                    assignmentRepo.deleteById(assignment.getId());
                });

        courseRepo.deleteById(courseId);
    }

    @Override
    public List<Announcement> getAnnouncementsForCourse(Long courseId) {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");

        if (!courseRepo.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        return announcementRepo.findAllByCourseId(courseId);
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        if (courseId == null) throw new IllegalArgumentException("Course ID cannot be null");

        return courseRepo.findById(courseId);
    }

    @Override
    public Optional<Course> getByIdWithLessons(Long courseId) {
        Optional<Course> course = getCourseById(courseId);
        course.ifPresent(c -> c.setLessons(lessonRepo.findAllByCourseId(courseId)));
        return course;
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        if (instructorId == null) throw new IllegalArgumentException("Instructor ID cannot be null");

        return courseRepo.findAllByInstructor_Id(instructorId);
    }

    @Override
    public List<Course> getCoursesByCategory(String category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");

        return courseRepo.findAllByCategory(category);
    }

    @Override
    public Optional<Course> getCourseByTitle(String title) {
        if (title == null) throw new IllegalArgumentException("Title cannot be null");

        return courseRepo.findByTitle(title);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }
}