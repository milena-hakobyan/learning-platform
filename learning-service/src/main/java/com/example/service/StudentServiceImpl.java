package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.feign.UserServiceClient;
import com.example.mapper.*;
import com.example.model.Assignment;
import com.example.model.Student;
import com.example.model.Submission;
import com.example.repository.*;
import feign.FeignException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final CourseManagementService courseManagementService;
    private final CourseEnrollmentService enrollmentService;
    private final JpaStudentRepository studentRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaGradeRepository gradeRepo;
    private final JpaAssignmentRepository assignmentRepo;

    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final MaterialMapper materialMapper;
    private final SubmissionMapper submissionMapper;
    private final GradeMapper gradeMapper;
    private final UserServiceClient userClient;

    public StudentServiceImpl(
            CourseManagementService courseManagementService,
            CourseEnrollmentService enrollmentService,
            JpaStudentRepository studentRepo,
            JpaSubmissionRepository submissionRepo,
            JpaLessonRepository lessonRepo,
            JpaGradeRepository gradeRepo,
            JpaAssignmentRepository assignmentRepo,
            StudentMapper studentMapper,
            CourseMapper courseMapper,
            MaterialMapper materialMapper,
            SubmissionMapper submissionMapper,
            GradeMapper gradeMapper,
            UserServiceClient userClient
    ) {
        this.courseManagementService = courseManagementService;
        this.enrollmentService = enrollmentService;
        this.studentRepo = studentRepo;
        this.submissionRepo = submissionRepo;
        this.lessonRepo = lessonRepo;
        this.gradeRepo = gradeRepo;
        this.assignmentRepo = assignmentRepo;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.materialMapper = materialMapper;
        this.submissionMapper = submissionMapper;
        this.gradeMapper = gradeMapper;
        this.userClient = userClient;
    }

    @Override
    public StudentResponse getStudentById(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        return studentRepo.findById(studentId)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    @Override
    public Page<StudentResponse> getAllStudents(Pageable pageable) {
        return studentRepo.findAll(pageable)
                .map(studentMapper::toDto);
    }
    @Override
    public Optional<GradeResponse> getAssignmentGradeForStudent(Long assignmentId, Long studentId) {
        if (!assignmentRepo.existsById(assignmentId)) {
            throw new ResourceNotFoundException("Assignment not found with ID: " + assignmentId);
        }
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        return gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .map(gradeMapper::toDto);
    }

    @Override
    public StudentResponse updateStudent(Long studentId, UpdateStudentRequest request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found"));
        studentMapper.updateEntity(request, student);
        studentRepo.save(student);
        return studentMapper.toDto(student);
    }

    @Override
    public Page<CourseResponse> getEnrolledCourses(Long studentId, Pageable pageable) {
        return studentRepo.findAllEnrolledCourses(studentId, pageable)
                .map(courseMapper::toDto);
    }

    @Override
    public Page<SubmissionResponse> getSubmissionsByStudentId(Long studentId, Pageable pageable) {
        return submissionRepo.findAllByStudentUserId(studentId, pageable)
                .map(submissionMapper::toDto);
    }

    @Override
    public Page<GradeResponse> getGradesForCourse(Long courseId, Long studentId, Pageable pageable) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        enrollmentService.ensureStudentEnrollment(courseId, studentId);
        return gradeRepo.findAllByStudentIdAndCourseId(studentId, courseId, pageable)
                .map(gradeMapper::toDto);
    }

    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with Id: " + studentId + " doesn't exist");
        }
        CourseResponse courseResponse = courseManagementService.getById(courseId);
        enrollmentService.enrollStudent(courseId, studentId);
        logActivity(studentId, "Enrolled in course: " + courseResponse.getTitle());
    }

    @Override
    public void dropCourse(Long studentId, Long courseId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with Id: " + studentId + " doesn't exist");
        }
        CourseResponse courseResponse = courseManagementService.getById(courseId);
        enrollmentService.ensureStudentEnrollment(studentId, courseId);
        enrollmentService.unenrollStudent(courseId, studentId);
        logActivity(studentId, "Dropped course: " + courseResponse.getTitle());
    }

    @Override
    public Page<CourseResponse> browseAvailableCourses(Pageable pageable) {
        return courseManagementService.getAll(pageable);
    }

    @Override
    public List<MaterialResponse> accessLessonMaterials(Long studentId, Long lessonId) {
        checkStudentExists(studentId);
        ensureStudentAccessToLesson(studentId, lessonId);
        return lessonRepo.findById(lessonId)
                .map(lesson -> {
                    logActivity(studentId, "Accessed materials for lesson ID: " + lessonId);
                    return lesson.getMaterials().stream().map(materialMapper::toDto).toList();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Lesson doesn't exist"));
    }

    @Override
    public List<MaterialResponse> accessAssignmentMaterials(Long studentId, Long assignmentId) {
        checkStudentExists(studentId);
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment doesn't exist"));
        enrollmentService.ensureStudentEnrollment(studentId, assignment.getCourse().getId());
        logActivity(studentId, "Accessed materials for assignment ID: " + assignmentId);
        return assignment.getMaterials().stream().map(materialMapper::toDto).toList();
    }

    @Override
    public void submitAssignment(Long studentId, CreateSubmissionRequest request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with given ID doesn't exist"));
        Assignment assignment = assignmentRepo.findById(request.getAssignmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
        enrollmentService.ensureStudentEnrollment(studentId, assignment.getCourse().getId());
        Submission submission = submissionMapper.toEntity(request, student, assignment);
        student.addSubmission(submission);
        submissionRepo.save(submission);
        logActivity(studentId, "Submitted assignment: " + assignment.getTitle());
    }

    private void checkStudentExists(Long studentId) {
        if (!studentRepo.existsById(studentId)) {
            throw new ResourceNotFoundException("Student with Id: " + studentId + " doesn't exist");
        }
    }

    private void logActivity(Long userId, String action) {
        try {
            userClient.logActivity(userId, new UserServiceClient.ActivityLogRequest(action));
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }

    public void ensureStudentAccessToLesson(Long studentId, Long lessonId) {
        boolean hasAccess = lessonRepo.existsByStudentIdAndLessonId(studentId, lessonId);
        if (!hasAccess) {
            throw new ResourceNotFoundException("Student " + studentId + " has no access to lesson " + lessonId);
        }
    }

    @Override
    public boolean hasSubmitted(Long studentId, Long assignmentId) {
        return submissionRepo.existsByStudentUserIdAndAssignmentId(studentId, assignmentId);
    }
}
