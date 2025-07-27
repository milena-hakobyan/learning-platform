package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CourseManagementServiceImpl implements CourseManagementService {

    private final CourseRepository courseRepo;
    private final LessonRepository lessonRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final AnnouncementRepository announcementRepo;

    public CourseManagementServiceImpl(CourseRepository courseRepo, LessonRepository lessonRepo, AssignmentRepository assignmentRepo,
                                       SubmissionRepository submissionRepo, AnnouncementRepository announcementRepo) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.announcementRepo = announcementRepo;
    }

    @Override
    public void createCourse(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");

        if (courseRepo.findByTitle(course.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Course with title '" + course.getTitle() + "' already exists.");
        }
        courseRepo.save(course);
    }

    @Override
    public void updateCourse(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        courseRepo.ensureCourseExists(course.getId());

        courseRepo.update(course);
    }

    @Override
    public void deleteCourse(Long courseId) {
        courseRepo.ensureCourseExists(courseId);

        assignmentRepo.findAllByCourseId(courseId)
                .forEach(assignment -> {
                    submissionRepo.findAllByAssignmentId(assignment.getId())
                            .forEach(submission -> submissionRepo.delete(submission.getId()));
                    assignmentRepo.delete(assignment.getId());
                });
        courseRepo.delete(courseId);
    }


    @Override
    public List<Announcement> getAnnouncementsForCourse(Long courseId) {
        courseRepo.ensureCourseExists(courseId);

        return announcementRepo.findAllByCourseId(courseId);
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        Objects.requireNonNull(courseId, "Course Id cannot be null");

        return courseRepo.findById(courseId);
    }

    @Override
    public Optional<Course> getByIdWithLessons(Long courseId) {
        Optional<Course> course = getCourseById(courseId);
        if (course.isPresent()) {
            List<Lesson> lessons = lessonRepo.findAllByCourseId(courseId);
            course.get().setLessons(lessons);
        }
        return course;
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        Objects.requireNonNull(instructorId, "InstructorId cannot be null");

        return courseRepo.findAllByInstructor(instructorId);
    }

    @Override
    public List<Course> getCoursesByCategory(String category) {
        Objects.requireNonNull(category, "Category cannot be null");

        return courseRepo.findAllByCategory(category);
    }

    @Override
    public Optional<Course> getCourseByTitle(String title) {
        Objects.requireNonNull(title, "Title cannot be null");

        return courseRepo.findByTitle(title);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

}