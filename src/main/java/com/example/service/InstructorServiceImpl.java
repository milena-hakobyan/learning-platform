package com.example.service;

import com.example.model.*;
import com.example.repository.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InstructorServiceImpl implements InstructorService {
    private final UserService userService;
    private final InstructorRepository instructorRepo;
    private final CourseService courseService;
    private final LessonRepository lessonRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final GradeRepository gradeRepo;
    private final AnnouncementRepository announcementRepo;
    private final ActivityLogRepository activityLogRepo;

    public InstructorServiceImpl(UserService userService, InstructorRepository instructorRepo, CourseService courseService,
                                 LessonRepository lessonRepo, AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo,
                                 GradeRepository gradeRepo, AnnouncementRepository announcementRepo, ActivityLogRepository activityLogRepo) {
        this.userService = userService;
        this.instructorRepo = instructorRepo;
        this.courseService = courseService;
        this.lessonRepo = lessonRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.announcementRepo = announcementRepo;
        this.activityLogRepo = activityLogRepo;
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    @Override
    public Optional<Instructor> getInstructorById(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return instructorRepo.findById(instructorId);
    }

    @Override
    public List<Course> getCoursesCreated(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return courseService.getCoursesByInstructor(instructorId);
    }

    @Override
    public List<Lesson> getLessonsCreated(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return lessonRepo.findLessonsByInstructorId(instructorId);
    }

    @Override
    public List<Assignment> getAssignmentsCreated(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return assignmentRepo.findAssignmentsByInstructorId(instructorId);
    }

    @Override
    public List<Announcement> getAnnouncementsPosted(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return announcementRepo.findByInstructorId(instructorId);
    }

    @Override
    public List<Submission> getSubmissionsForAssignment(Integer instructorId, Integer assignmentId) {
        instructorRepo.ensureInstructorExists(instructorId);
        assignmentRepo.ensureAssignmentExists(assignmentId);

        List<Submission> submissions = submissionRepo.findByAssignmentId(assignmentId);

        activityLogRepo.save(new ActivityLog(instructorId, "Viewed submissions for assignment with id : " + assignmentId));
        return submissions;
    }

    @Override
    public void createCourse(Course course) {
        courseService.createCourse(course);

        activityLogRepo.save(new ActivityLog(course.getInstructorId(), "Created course: " + course.getTitle()));
    }

    @Override
    public void deleteCourse(Integer instructorId, Integer courseId) {
        instructorRepo.ensureInstructorExists(instructorId);

        Course course = requireAuthorizedCourse(instructorId, courseId);

        courseService.deleteCourse(courseId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted course: " + course.getTitle()));
    }

    @Override
    public void createAssignment(Integer instructorId, Integer courseId, Assignment assignment) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(assignment);

        requireAuthorizedCourse(instructorId, courseId);

        courseService.addAssignmentToCourse(courseId, assignment);
        activityLogRepo.save(new ActivityLog(instructorId, "Created assignment: " + assignment.getTitle()));
    }

    @Override
    public void deleteAssignment(Integer instructorId, Integer courseId, Integer assignmentId) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(assignmentId);

        Course course = requireAuthorizedCourse(instructorId, courseId);

        courseService.removeAssignmentFromCourse(courseId, assignmentId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted assignment ID: " + assignmentId + " from course: " + course.getTitle()));
    }

    @Override
    public void createLesson(Integer instructorId, Integer courseId, Lesson lesson) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(lesson);

        Course course = requireAuthorizedCourse(instructorId, courseId);

        courseService.addLessonToCourse(courseId, lesson);
        activityLogRepo.save(new ActivityLog(instructorId, "Created lesson: " + lesson.getTitle() + " in course: " + course.getTitle()));
    }

    @Override
    public void deleteLesson(Integer instructorId, Integer courseId, Integer lessonId) {
        instructorRepo.ensureInstructorExists(instructorId);
        lessonRepo.ensureLessonExists(lessonId);

        Course course = requireAuthorizedCourse(instructorId, courseId);

        courseService.removeLessonFromCourse(courseId, lessonId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted lesson ID: " + lessonId + " from course: " + course.getTitle()));
    }

    @Override
    public void uploadMaterial(Integer instructorId, Integer lessonId, Material material) {
        instructorRepo.ensureInstructorExists(instructorId);
        lessonRepo.ensureLessonExists(lessonId);
        requireAuthorizedCourse(instructorId, lessonId);
        Objects.requireNonNull(material);

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        courseService.addMaterialToLesson(lessonId, material);
        activityLogRepo.save(new ActivityLog(instructorId, "Uploaded material: " + material.getTitle() + " to lesson: " + found.getTitle()));
    }

    @Override
    public void deleteMaterial(Integer instructorId, Integer lessonId, Integer materialId) {
        lessonRepo.ensureLessonExists(lessonId);
        instructorRepo.ensureInstructorExists(instructorId);

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        courseService.removeMaterialFromLesson(lessonId, materialId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted material: " + materialId + " from lesson: " + found.getTitle()));
    }

    @Override
    public void gradeSubmission(Integer instructorId, Integer submissionId, Grade grade) {
        instructorRepo.ensureInstructorExists(instructorId);
        submissionRepo.ensureSubmissionExists(submissionId);
        Objects.requireNonNull(grade, "Grade cannot be null");

        Submission submission = submissionRepo.findById(submissionId)
                .orElseThrow(() ->  new IllegalArgumentException("Submission not found"));

        submission.setGradeId(grade.getGradeId());
        submission.setStatus(SubmissionStatus.GRADED);
        gradeRepo.save(grade);
        submissionRepo.update(submission);
        activityLogRepo.save(new ActivityLog(instructorId, "Graded submission ID: " + submission.getSubmissionId()));
    }

    @Override
    public void sendAnnouncement(Integer instructorId, Integer courseId, String title, String message) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");

        Course course = requireAuthorizedCourse(instructorId, courseId);

        Announcement announcement = new Announcement(null, title, message, instructorId, courseId);
        announcementRepo.save(announcement);
        activityLogRepo.save(new ActivityLog(instructorId, "Sent announcement to course: " + course.getTitle() + " - Title: " + title));
    }

    private Course requireAuthorizedCourse(Integer instructorId, Integer courseId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");

        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new SecurityException("Instructor is not authorized to access this course");
        }

        return course;
    }

}