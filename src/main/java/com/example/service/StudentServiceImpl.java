package com.example.service;

import com.example.dto.course.CourseResponse;
import com.example.dto.grade.GradeResponse;
import com.example.dto.material.MaterialResponse;
import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.dto.submission.CreateSubmissionRequest;
import com.example.dto.submission.SubmissionResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.*;
import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final CourseManagementService courseManagementService;
    private final CourseEnrollmentService enrollmentService;
    private final JpaStudentRepository studentRepo;
    private final JpaCourseRepository courseRepo;
    private final JpaUserRepository userRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaGradeRepository gradeRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaActivityLogRepository activityLogRepo;

    private final StudentMapper studentMapper;
    private final CourseMapper courseMapper;
    private final MaterialMapper materialMapper;
    private final SubmissionMapper submissionMapper;
    private final GradeMapper gradeMapper;


    public StudentServiceImpl(CourseManagementService courseManagementService, CourseEnrollmentService enrollmentService, JpaStudentRepository studentRepo, JpaCourseRepository courseRepo, JpaUserRepository userRepo, JpaSubmissionRepository submissionRepo, JpaLessonRepository lessonRepo, JpaGradeRepository gradeRepo, JpaAssignmentRepository assignmentRepo, JpaActivityLogRepository activityLogRepo, StudentMapper studentMapper, CourseMapper courseMapper, MaterialMapper materialMapper, SubmissionMapper submissionMapper, GradeMapper gradeMapper) {
        this.courseManagementService = courseManagementService;
        this.enrollmentService = enrollmentService;
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
        this.submissionRepo = submissionRepo;
        this.lessonRepo = lessonRepo;
        this.gradeRepo = gradeRepo;
        this.assignmentRepo = assignmentRepo;
        this.activityLogRepo = activityLogRepo;
        this.studentMapper = studentMapper;
        this.courseMapper = courseMapper;
        this.materialMapper = materialMapper;
        this.submissionMapper = submissionMapper;
        this.gradeMapper = gradeMapper;
    }

    @Override
    public StudentResponse getStudentById(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        return studentRepo.findById(studentId)
                .map(studentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));
    }

    @Override
    public StudentResponse updateStudent(Long studentId, UpdateStudentRequest request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with ID " + studentId + " not found"));

        studentMapper.updateEntity(request, student); // your mapper handles non-null updates
        studentRepo.save(student);

        return studentMapper.toDto(student);
    }


    @Override
    public List<CourseResponse> getEnrolledCourses(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        return studentRepo.findAllEnrolledCourses(studentId)
                .stream()
                .map(courseMapper::toDto)
                .toList();
    }

    @Override
    public List<SubmissionResponse> getSubmissionsByStudentId(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        return submissionRepo.findAllByStudentId(studentId)
                .stream()
                .map(submissionMapper::toDto)
                .toList();
    }

    @Override
    public List<GradeResponse> getGradesForCourse(Long courseId, Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        enrollmentService.ensureStudentEnrollment(courseId, studentId);

        return gradeRepo.findAllByStudentIdAndCourseId(studentId, courseId)
                .stream()
                .map(gradeMapper::toDto)
                .toList();
    }

    @Override
    public Optional<GradeResponse> getAssignmentGradeForStudent(Long assignmentId, Long studentId) {
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        if (!assignmentRepo.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }

        return gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .map(gradeMapper::toDto);
    }

    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        CourseResponse courseResponse = courseManagementService.getById(courseId);

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        enrollmentService.enrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(user, "Enrolled in course: " + courseResponse.getTitle()));
    }


    @Override
    public void dropCourse(Long studentId, Long courseId) {
        Objects.requireNonNull(studentId);

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        CourseResponse courseResponse = courseManagementService.getById(courseId);

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        enrollmentService.ensureStudentEnrollment(studentId, courseId);

        enrollmentService.unenrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(user, "Dropped course: " + courseResponse.getTitle()));
    }


    @Override
    public List<CourseResponse> browseAvailableCourses() {

        return courseManagementService.getAll();
    }

    @Override
    public List<MaterialResponse> accessLessonMaterials(Long studentId, Long lessonId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        Lesson lesson = lessonRepo.findById(lessonId)
                        .orElseThrow(()-> new IllegalArgumentException("Lesson doesn't exist"));

        ensureStudentAccessToLesson(studentId, lessonId);

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        activityLogRepo.save(new ActivityLog(user, "Accessed materials for lesson ID: " + lessonId));
        return lesson.getMaterials()
                .stream()
                .map(materialMapper::toDto)
                .toList();

    }

    @Override
    public List<MaterialResponse> accessAssignmentMaterials(Long studentId, Long assignmentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment doesn't exist"));

        enrollmentService.ensureStudentEnrollment(studentId, assignment.getCourse().getId());

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        activityLogRepo.save(new ActivityLog(user, "Accessed materials for assignment ID: " + assignmentId));

        return assignment.getMaterials()
                .stream()
                .map(materialMapper::toDto)
                .toList();
    }

    @Override
    public void submitAssignment(Long studentId, CreateSubmissionRequest request) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(()-> new IllegalArgumentException("Student with given ID doesn't exist"));

        Assignment assignment = assignmentRepo.findById(request.getAssignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        Course course = courseRepo.findById(assignment.getCourse().getId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        enrollmentService.ensureStudentEnrollment(studentId, course.getId());

        Submission submission = submissionMapper.toEntity(request, student, assignment);
        student.addSubmission(submission);

        submissionRepo.save(submission);
        activityLogRepo.save(new ActivityLog(user, "Submitted assignment: " + assignment.getTitle()));
    }

    public void ensureStudentAccessToLesson(Long studentId, Long lessonId) {
        boolean hasAccess = lessonRepo.existsByStudentIdAndLessonId(studentId, lessonId);
        if (!hasAccess) {
            throw new IllegalArgumentException("Student " + studentId + " has no access to lesson " + lessonId);
        }
    }

    @Override
    public boolean hasSubmitted(Long studentId, Long assignmentId) {
        return submissionRepo.existsByStudentIdAndAssignmentId(studentId, assignmentId);
    }
}