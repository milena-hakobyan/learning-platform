package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorContentServiceImpl implements InstructorContentService {
    private final JpaInstructorRepository instructorRepo;
    private final JpaUserRepository userRepository;
    private final AssignmentService assignmentService;
    private final JpaLessonRepository lessonRepo;
    private final LessonService lessonService;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaActivityLogRepository activityLogRepo;
    private final InstructorAuthorizationService instructorService;


    public InstructorContentServiceImpl(JpaInstructorRepository instructorRepo, JpaUserRepository userRepository, AssignmentService assignmentService, JpaLessonRepository lessonRepo, LessonService lessonService, JpaAssignmentRepository assignmentRepo, JpaActivityLogRepository activityLogRepo, InstructorAuthorizationService instructorService) {
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
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        return lessonRepo.findAllLessonsByInstructorId(instructorId);
    }

    @Override
    public List<Assignment> getAssignmentsCreated(Long instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found with ID: " + instructorId);
        }
        return assignmentRepo.findAllByInstructorId(instructorId);
    }


    @Override
    public void createAssignment(Long instructorId, Long courseId, Assignment assignment) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(assignment, "Assignment cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found");
        }

        instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        assignmentService.addAssignmentToCourse(courseId, assignment);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Created assignment: " + assignment.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteAssignment(Long instructorId, Long courseId, Long assignmentId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        assignmentService.removeAssignmentFromCourse(courseId, assignmentId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted assignment ID: " + assignmentId + " from course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void createLesson(Long instructorId, Long courseId, Lesson lesson) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(lesson, "Lesson cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.addLessonToCourse(courseId, lesson);

        ActivityLog activityLog = new ActivityLog(user, "Created lesson: " + lesson.getTitle() + " in course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteLesson(Long instructorId, Long courseId, Long lessonId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found");
        }

        if (!lessonRepo.existsById(lessonId)) {
            throw new IllegalArgumentException("Lesson not found with ID: " + lessonId);
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.removeLessonFromCourse(courseId, lessonId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted lesson ID: " + lessonId + " from course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public void uploadMaterial(Long instructorId, Long lessonId, Material material) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");
        Objects.requireNonNull(material, "Material cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found");
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User instructorUser = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, lesson.getCourse().getId());

        lessonService.addMaterialToLesson(lessonId, material);

        ActivityLog activityLog = new ActivityLog(
                instructorUser,
                "Uploaded material: " + material.getTitle() + " to lesson: " + lesson.getTitle()
        );
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteMaterial(Long instructorId, Long lessonId, Long materialId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");
        Objects.requireNonNull(materialId, "Material ID cannot be null");

        if (!instructorRepo.existsById(instructorId)) {
            throw new IllegalArgumentException("Instructor not found");
        }

        if (!lessonRepo.existsById(lessonId)) {
            throw new IllegalArgumentException("Lesson not found with ID: " + lessonId);
        }

        Lesson found = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found"));

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        lessonService.removeMaterialFromLesson(lessonId, materialId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted material: " + materialId + " from lesson: " + found.getTitle());
        activityLogRepo.save(activityLog);
    }
}