package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorContentServiceImpl implements InstructorContentService {
    private final InstructorRepository instructorRepo;
    private final UserRepository userRepository;
    private final AssignmentService assignmentService;
    private final LessonRepository lessonRepo;
    private final LessonService lessonService;
    private final AssignmentRepository assignmentRepo;
    private final ActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorService;


    public InstructorContentServiceImpl(InstructorRepository instructorRepo, UserRepository userRepository, AssignmentService assignmentService, LessonRepository lessonRepo, LessonService lessonService, AssignmentRepository assignmentRepo, ActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService) {
        this.instructorRepo = instructorRepo;
        this.userRepository = userRepository;
        this.assignmentService = assignmentService;
        this.lessonRepo = lessonRepo;
        this.lessonService = lessonService;
        this.assignmentRepo = assignmentRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorService = instructorService;
    }

    @Override
    public List<Lesson> getLessonsCreated(Long instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return lessonRepo.findAllLessonsByInstructorId(instructorId);
    }

    @Override
    public List<Assignment> getAssignmentsCreated(Long instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return assignmentRepo.findAllAssignmentsByInstructorId(instructorId);
    }


    @Override
    public void createAssignment(Long instructorId, Long courseId, Assignment assignment) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(assignment);

        instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        assignmentService.addAssignmentToCourse(courseId, assignment);

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Created assignment: " + assignment.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteAssignment(Long instructorId, Long courseId, Long assignmentId) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(assignmentId);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        assignmentService.removeAssignmentFromCourse(courseId, assignmentId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted assignment ID: " + assignmentId + " from course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void createLesson(Long instructorId, Long courseId, Lesson lesson) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(lesson);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.addLessonToCourse(courseId, lesson);

        ActivityLog activityLog = new ActivityLog(user, "Created lesson: " + lesson.getTitle() + " in course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteLesson(Long instructorId, Long courseId, Long lessonId) {
        instructorRepo.ensureInstructorExists(instructorId);
        lessonRepo.ensureLessonExists(lessonId);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.removeLessonFromCourse(courseId, lessonId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted lesson ID: " + lessonId + " from course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void uploadMaterial(Long instructorId, Long lessonId, Material material) {
        instructorRepo.ensureInstructorExists(instructorId);
        lessonRepo.ensureLessonExists(lessonId);
        instructorService.ensureAuthorizedCourseAccess(instructorId, lessonId);
        Objects.requireNonNull(material);

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.addMaterialToLesson(lessonId, material);

        ActivityLog activityLog = new ActivityLog(user, "Uploaded material: " + material.getTitle() + " to lesson: " + found.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteMaterial(Long instructorId, Long lessonId, Long materialId) {
        lessonRepo.ensureLessonExists(lessonId);
        instructorRepo.ensureInstructorExists(instructorId);

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.removeMaterialFromLesson(lessonId, materialId);

        ActivityLog activityLog = new ActivityLog(user, "Uploaded material: " + materialId + " from lesson: " + found.getTitle());
        activityLogRepo.save(activityLog);
    }
}