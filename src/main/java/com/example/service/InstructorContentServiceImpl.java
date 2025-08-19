package com.example.service;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.dto.lesson.CreateLessonRequest;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.lesson.UpdateLessonRequest;
import com.example.dto.material.CreateMaterialRequest;
import com.example.dto.material.MaterialResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class InstructorContentServiceImpl implements InstructorContentService {
    private final LessonService lessonService;
    private final AssignmentService assignmentService;

    private final JpaInstructorRepository instructorRepo;
    private final JpaUserRepository userRepository;
    private final JpaLessonRepository lessonRepo;
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
    public AssignmentResponse createAssignment(Long instructorId, Long courseId, CreateAssignmentRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        AssignmentResponse assignmentResponse = assignmentService.createAssignment(courseId, request);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Created assignment: " + request.getTitle());
        activityLogRepo.save(activityLog);

        return assignmentResponse;
    }


    @Override
    public AssignmentResponse updateAssignment(Long instructorId, Long assignmentId, UpdateAssignmentRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));

        instructorService.ensureAuthorizedCourseAccess(instructorId, assignment.getCourse().getId());

        AssignmentResponse updatedAssignment = assignmentService.updateAssignment(assignmentId, request);

        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ActivityLog activityLog = new ActivityLog(user, "Updated assignment: " + assignment.getTitle());
        activityLogRepo.save(activityLog);

        return updatedAssignment;
    }

    @Override
    public void deleteAssignment(Long instructorId, Long courseId, Long assignmentId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assignmentService.deleteAssignment(assignmentId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted assignment ID: " + assignmentId + " from course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public LessonResponse createLesson(Long instructorId, Long courseId, CreateLessonRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LessonResponse lessonResponse = lessonService.addLessonToCourse(courseId, request);

        ActivityLog activityLog = new ActivityLog(user, "Created lesson: " + request.getTitle() + " in course: " + course.getTitle());
        activityLogRepo.save(activityLog);

        return lessonResponse;
    }


    @Override
    public LessonResponse updateLesson(Long instructorId, Long lessonId, UpdateLessonRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with ID: " + lessonId));

        instructorService.ensureAuthorizedCourseAccess(instructorId, lesson.getCourse().getId());

        User user = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LessonResponse updatedLesson = lessonService.updateLesson(lessonId, request);

        ActivityLog log = new ActivityLog(user, "Updated lesson: " + updatedLesson.getTitle());
        activityLogRepo.save(log);

        return updatedLesson;
    }

    @Override
    public void deleteLesson(Long instructorId, Long courseId, Long lessonId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        if (!lessonRepo.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson not found with ID: " + lessonId);
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);

        User user = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        lessonService.removeLessonFromCourse(courseId, lessonId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted lesson ID: " + lessonId + " from course: " + course.getTitle());
        activityLogRepo.save(activityLog);
    }

    @Override
    public MaterialResponse addMaterialToLesson(Long instructorId, Long lessonId, CreateMaterialRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        User instructorUser = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, lesson.getCourse().getId());

        MaterialResponse materialResponse = lessonService.addMaterialToLesson(lessonId, request);

        ActivityLog activityLog = new ActivityLog(
                instructorUser,
                "Uploaded material: " + request.getTitle() + " to lesson: " + lesson.getTitle()
        );
        activityLogRepo.save(activityLog);

        return materialResponse;
    }

    @Override
    public MaterialResponse addMaterialToAssignment(Long instructorId, Long assignmentId, CreateMaterialRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        User instructorUser = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, assignment.getCourse().getId());

        MaterialResponse materialResponse = assignmentService.addMaterialToAssignment(assignmentId, request);

        ActivityLog activityLog = new ActivityLog(
                instructorUser,
                "Uploaded material: " + request.getTitle() + " to assignment: " + assignment.getTitle()
        );
        activityLogRepo.save(activityLog);

        return materialResponse;
    }

    @Override
    public void deleteMaterialFromLesson(Long instructorId, Long lessonId, Long materialId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        lessonService.removeMaterialFromLesson(lessonId, materialId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted material: " + materialId + " from lesson with ID " + lessonId);
        activityLogRepo.save(activityLog);
    }

    @Override
    public void deleteMaterialFromAssignment(Long instructorId, Long assignmentId, Long materialId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        assignmentService.removeMaterialFromAssignment(assignmentId, materialId);

        ActivityLog activityLog = new ActivityLog(user, "Deleted material: " + materialId + " from assignment with ID " + assignmentId);
        activityLogRepo.save(activityLog);
    }
}