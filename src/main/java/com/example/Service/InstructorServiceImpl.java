package com.example.Service;

import com.example.Model.*;
import com.example.Repository.*;
import com.example.Utils.InputValidationUtils;

import java.util.List;
import java.util.Optional;

public class InstructorServiceImpl implements InstructorService {
    private final UserService userService;
    private final InstructorRepository instructorRepo;
    private final CourseService courseService;
    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final GradeRepository gradeRepo;
    private final AnnouncementRepository announcementRepo;

    public InstructorServiceImpl(UserService userService, InstructorRepository instructorRepo, CourseService courseService, LessonRepository lessonRepository,
                                 AssignmentRepository assignmentRepo,
                                 SubmissionRepository submissionRepo,
                                 GradeRepository gradeRepo,
                                 AnnouncementRepository announcementRepo) {
        this.userService = userService;
        this.instructorRepo = instructorRepo;
        this.courseService = courseService;
        this.lessonRepository = lessonRepository;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.gradeRepo = gradeRepo;
        this.announcementRepo = announcementRepo;
    }

    @Override
    public List<Instructor> getAllInstructors(){
        return instructorRepo.findAll();
    }

    @Override
    public Optional<Instructor> getInstructorById(Integer instructorId){
        InputValidationUtils.validateInstructorExists(instructorId, userService);

        return instructorRepo.findById(instructorId);
    }

    @Override
    public List<Course> getCoursesCreated(Integer instructorId) {
        InputValidationUtils.validateInstructorExists(instructorId, userService);

        return courseService.getCoursesByInstructor(instructorId);
    }

    @Override
    public List<Lesson> getLessonsCreated(Integer instructorId) {
        InputValidationUtils.validateInstructorExists(instructorId, userService);

        return lessonRepository.findLessonsByInstructorId(instructorId);
    }

    @Override
    public List<Assignment> getAssignmentsCreated(Integer instructorId) {
        InputValidationUtils.validateInstructorExists(instructorId, userService);

        return assignmentRepo.findAssignmentsByInstructorId(instructorId);
    }

    @Override
    public List<Announcement> getAnnouncementsPosted(Integer instructorId) {
        InputValidationUtils.validateInstructorExists(instructorId, userService);

        return announcementRepo.findByInstructorId(instructorId);
    }


    @Override
    public void createCourse(Course course) {
        courseService.createCourse(course);
    }

    @Override
    public void deleteCourse(Integer courseId) {
        courseService.deleteCourse(courseId);
    }

    @Override
    public void createAssignment(Integer courseId, Assignment assignment) {
        courseService.addAssignmentToCourse(courseId, assignment);
    }

    @Override
    public void deleteAssignment(Integer courseId, Integer assignmentId) {
        courseService.removeAssignmentFromCourse(courseId, assignmentId);
    }

    @Override
    public void createLesson(Integer courseId, Lesson lesson) {
        courseService.addLessonToCourse(courseId, lesson);
    }

    @Override
    public void deleteLesson(Integer courseId, Integer lessonId){
        courseService.removeLessonFromCourse(courseId, lessonId);
    }

    @Override
    public void uploadMaterial(Lesson lesson, Material material) {
        courseService.addMaterialToLesson(lesson.getLessonId(), material);
    }

    @Override
    public void deleteMaterial(Lesson lesson, Material material) {
        courseService.removeMaterialFromLesson(lesson.getLessonId(), material.getMaterialId());
    }

    @Override
    public List<Submission> getSubmissionsForAssignment(Integer assignmentId) {
        InputValidationUtils.requireAssignmentExists(assignmentId, assignmentRepo);

        return submissionRepo.findByAssignmentId(assignmentId);
    }


    @Override
    public void gradeSubmission(Submission submission, Grade grade) {
        InputValidationUtils.requireSubmissionExists(submission.getSubmissionId(), submissionRepo);
        InputValidationUtils.requireNonNull(grade, "Grade cannot be null");

        submission.setGradeId(grade.getGradeId());
        submission.setStatus(SubmissionStatus.GRADED);
        gradeRepo.save(grade);
        submissionRepo.update(submission);
    }

    @Override
    public void sendAnnouncement(Course course, Integer id, String title, String message) {
        InputValidationUtils.requireNonNull(course, "Course cannot be null");
        InputValidationUtils.requireNonNull(course.getCourseId(), "Course ID cannot be null");
        InputValidationUtils.requireNonNull(title, "Title cannot be null");
        InputValidationUtils.requireNonNull(message, "Message cannot be null");

        Announcement announcement = new Announcement(id, title, message, course.getInstructorId(), course.getCourseId());
        course.postAnnouncement(announcement);
        announcementRepo.save(announcement);
    }
}