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
import com.example.feign.UserServiceClient;
import com.example.model.Assignment;
import com.example.model.Course;
import com.example.model.Lesson;
import com.example.repository.JpaAssignmentRepository;
import com.example.repository.JpaInstructorRepository;
import com.example.repository.JpaLessonRepository;
import org.springframework.stereotype.Service;

@Service
public class InstructorContentServiceImpl implements InstructorContentService {

    private final LessonService lessonService;
    private final AssignmentService assignmentService;
    private final JpaInstructorRepository instructorRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final InstructorAuthorizationService instructorService;
    private final UserServiceClient userClient;

    public InstructorContentServiceImpl(JpaInstructorRepository instructorRepo,
                                        AssignmentService assignmentService,
                                        JpaLessonRepository lessonRepo,
                                        LessonService lessonService,
                                        JpaAssignmentRepository assignmentRepo,
                                        InstructorAuthorizationService instructorService,
                                        UserServiceClient userClient) {
        this.instructorRepo = instructorRepo;
        this.assignmentService = assignmentService;
        this.lessonRepo = lessonRepo;
        this.lessonService = lessonService;
        this.assignmentRepo = assignmentRepo;
        this.instructorService = instructorService;
        this.userClient = userClient;
    }

    private void logActivity(Long userId, String action) {
        UserServiceClient.ActivityLogRequest request = new UserServiceClient.ActivityLogRequest(action);
        userClient.logActivity(userId, request);
    }

    @Override
    public AssignmentResponse createAssignment(Long instructorId, Long courseId, CreateAssignmentRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);
        AssignmentResponse assignmentResponse = assignmentService.createAssignment(courseId, request);

        logActivity(instructorId, "Created assignment: " + request.getTitle());
        return assignmentResponse;
    }

    @Override
    public AssignmentResponse updateAssignment(Long instructorId, Long assignmentId, UpdateAssignmentRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, assignment.getCourse().getId());
        AssignmentResponse updated = assignmentService.updateAssignment(assignmentId, request);

        logActivity(instructorId, "Updated assignment: " + updated.getTitle());
        return updated;
    }

    @Override
    public void deleteAssignment(Long instructorId, Long courseId, Long assignmentId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);
        assignmentService.deleteAssignment(assignmentId);

        logActivity(instructorId, "Deleted assignment ID: " + assignmentId + " from course: " + course.getTitle());
    }

    @Override
    public LessonResponse createLesson(Long instructorId, Long courseId, CreateLessonRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);
        LessonResponse lessonResponse = lessonService.addLessonToCourse(courseId, request);

        logActivity(instructorId, "Created lesson: " + request.getTitle() + " in course: " + course.getTitle());
        return lessonResponse;
    }

    @Override
    public LessonResponse updateLesson(Long instructorId, Long lessonId, UpdateLessonRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, lesson.getCourse().getId());
        LessonResponse updated = lessonService.updateLesson(lessonId, request);

        logActivity(instructorId, "Updated lesson: " + updated.getTitle());
        return updated;
    }

    @Override
    public void deleteLesson(Long instructorId, Long courseId, Long lessonId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        if (!lessonRepo.existsById(lessonId)) {
            throw new ResourceNotFoundException("Lesson not found");
        }

        Course course = instructorService.ensureAuthorizedCourseAccess(instructorId, courseId);
        lessonService.removeLessonFromCourse(courseId, lessonId);

        logActivity(instructorId, "Deleted lesson ID: " + lessonId + " from course: " + course.getTitle());
    }

    @Override
    public MaterialResponse addMaterialToLesson(Long instructorId, Long lessonId, CreateMaterialRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, lesson.getCourse().getId());
        MaterialResponse materialResponse = lessonService.addMaterialToLesson(lessonId, request);

        logActivity(instructorId, "Uploaded material: " + request.getTitle() + " to lesson: " + lesson.getTitle());
        return materialResponse;
    }

    @Override
    public MaterialResponse addMaterialToAssignment(Long instructorId, Long assignmentId, CreateMaterialRequest request) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));

        instructorService.ensureAuthorizedCourseAccess(instructorId, assignment.getCourse().getId());
        MaterialResponse response = assignmentService.addMaterialToAssignment(assignmentId, request);

        logActivity(instructorId, "Uploaded material: " + request.getTitle() + " to assignment: " + assignment.getTitle());
        return response;
    }

    @Override
    public void deleteMaterialFromLesson(Long instructorId, Long lessonId, Long materialId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        lessonService.removeMaterialFromLesson(lessonId, materialId);
        logActivity(instructorId, "Deleted material: " + materialId + " from lesson with ID " + lessonId);
    }

    @Override
    public void deleteMaterialFromAssignment(Long instructorId, Long assignmentId, Long materialId) {
        if (!instructorRepo.existsById(instructorId)) {
            throw new ResourceNotFoundException("Instructor not found");
        }

        assignmentService.removeMaterialFromAssignment(assignmentId, materialId);
        logActivity(instructorId, "Deleted material: " + materialId + " from assignment with ID " + assignmentId);
    }
}
