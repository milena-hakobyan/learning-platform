package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorContentServiceImpl implements InstructorContentService {
    private final InstructorRepository instructorRepo;
    private final AssignmentService assignmentService;
    private final LessonRepository lessonRepo;
    private final LessonService lessonService;
    private final AssignmentRepository assignmentRepo;
    private final ActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorService;


    public InstructorContentServiceImpl(UserService userService, InstructorRepository instructorRepo, AssignmentService assignmentService, LessonRepository lessonRepo, LessonService lessonService, AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo, GradeRepository gradeRepo, ActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService) {
        this.instructorRepo = instructorRepo;
        this.assignmentService = assignmentService;
        this.lessonRepo = lessonRepo;
        this.lessonService = lessonService;
        this.assignmentRepo = assignmentRepo;
        this.activityLogRepo = activityLogRepo;
        this.instructorService = instructorService;
    }

    @Override
    public List<Lesson> getLessonsCreated(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return lessonRepo.findAllLessonsByInstructorId(instructorId);
    }

    @Override
    public List<Assignment> getAssignmentsCreated(Integer instructorId) {
        instructorRepo.ensureInstructorExists(instructorId);

        return assignmentRepo.findAllAssignmentsByInstructorId(instructorId);
    }


    @Override
    public void createAssignment(Integer instructorId, Integer courseId, Assignment assignment) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(assignment);

        instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        assignmentService.addAssignmentToCourse(courseId, assignment);
        activityLogRepo.save(new ActivityLog(instructorId, "Created assignment: " + assignment.getTitle()));
    }

    @Override
    public void deleteAssignment(Integer instructorId, Integer courseId, Integer assignmentId) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(assignmentId);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        assignmentService.removeAssignmentFromCourse(courseId, assignmentId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted assignment ID: " + assignmentId + " from course: " + course.getTitle()));
    }

    @Override
    public void createLesson(Integer instructorId, Integer courseId, Lesson lesson) {
        instructorRepo.ensureInstructorExists(instructorId);
        Objects.requireNonNull(lesson);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        lessonService.addLessonToCourse(courseId, lesson);
        activityLogRepo.save(new ActivityLog(instructorId, "Created lesson: " + lesson.getTitle() + " in course: " + course.getTitle()));
    }

    @Override
    public void deleteLesson(Integer instructorId, Integer courseId, Integer lessonId) {
        instructorRepo.ensureInstructorExists(instructorId);
        lessonRepo.ensureLessonExists(lessonId);

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        lessonService.removeLessonFromCourse(courseId, lessonId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted lesson ID: " + lessonId + " from course: " + course.getTitle()));
    }

    @Override
    public void uploadMaterial(Integer instructorId, Integer lessonId, Material material) {
        instructorRepo.ensureInstructorExists(instructorId);
        lessonRepo.ensureLessonExists(lessonId);
        instructorService.ensureAuthorizedCourseAccess(instructorId, lessonId);
        Objects.requireNonNull(material);

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lessonService.addMaterialToLesson(lessonId, material);
        activityLogRepo.save(new ActivityLog(instructorId, "Uploaded material: " + material.getTitle() + " to lesson: " + found.getTitle()));
    }

    @Override
    public void deleteMaterial(Integer instructorId, Integer lessonId, Integer materialId) {
        lessonRepo.ensureLessonExists(lessonId);
        instructorRepo.ensureInstructorExists(instructorId);

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        lessonService.removeMaterialFromLesson(lessonId, materialId);
        activityLogRepo.save(new ActivityLog(instructorId, "Deleted material: " + materialId + " from lesson: " + found.getTitle()));
    }

}
