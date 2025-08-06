package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final CourseManagementService courseManagementService;
    private final CourseEnrollmentService enrollmentService;
    private final JpaStudentRepository studentRepo;
    private final JpaUserRepository userRepo;
    private final JpaSubmissionRepository submissionRepo;
    private final JpaLessonRepository lessonRepo;
    private final JpaGradeRepository gradeRepo;
    private final JpaAssignmentRepository assignmentRepo;
    private final JpaActivityLogRepository activityLogRepo;


    public StudentServiceImpl(CourseManagementService courseManagementService, CourseEnrollmentService enrollmentService, JpaStudentRepository studentRepo, JpaUserRepository userRepo, JpaSubmissionRepository submissionRepo, JpaLessonRepository lessonRepo, JpaGradeRepository gradeRepo, JpaAssignmentRepository assignmentRepo, JpaActivityLogRepository activityLogRepo) {
        this.courseManagementService = courseManagementService;
        this.enrollmentService = enrollmentService;
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
        this.submissionRepo = submissionRepo;
        this.lessonRepo = lessonRepo;
        this.gradeRepo = gradeRepo;
        this.assignmentRepo = assignmentRepo;
        this.activityLogRepo = activityLogRepo;
    }

    @Override
    public Optional<Student> getStudentById(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        return studentRepo.findById(studentId);
    }

    @Override
    public List<Course> getEnrolledCourses(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        return studentRepo.findAllEnrolledCourses(studentId);
    }

    @Override
    public List<Submission> getSubmissionsByStudentId(Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        return submissionRepo.findAllByStudentId(studentId);
    }

    @Override
    public List<Grade> getGradesForCourse(Long courseId, Long studentId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        enrollmentService.ensureStudentEnrollment(courseId, studentId);

        return gradeRepo.findAllByStudentIdAndCourseId(studentId, courseId);
    }

    @Override
    public Optional<Grade> getAssignmentGradeForStudent(Long assignmentId, Long studentId) {
        Objects.requireNonNull(assignmentId, "Assignment ID cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        if (!assignmentRepo.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment not found with ID: " + assignmentId);
        }

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student not found with ID: " + studentId);
        }


        return gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        Course course = courseManagementService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        enrollmentService.enrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(user, "Enrolled in course: " + course.getTitle()));
    }


    @Override
    public void dropCourse(Long studentId, Long courseId) {
        Objects.requireNonNull(studentId);

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        Course course = courseManagementService.getCourseById(courseId).get();

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        enrollmentService.ensureStudentEnrollment(studentId, courseId);

        enrollmentService.unenrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(user, "Dropped course: " + course.getTitle()));
    }


    @Override
    public List<Course> browseAvailableCourses() {
        return courseManagementService.getAllCourses();
    }

    @Override
    public List<Material> accessMaterials(Long studentId, Long lessonId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");

        if (!studentRepo.existsById(studentId)) {
            throw new IllegalArgumentException("Student with Id: " + studentId + " doesn't exist");
        }

        if (!lessonRepo.existsById(lessonId)) {
            throw new IllegalArgumentException("Lesson with Id: " + lessonId + " doesn't exist");
        }

        ensureStudentAccessToLesson(studentId, lessonId);

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        activityLogRepo.save(new ActivityLog(user, "Accessed materials for lesson ID: " + lessonId));
        return lessonRepo.findAllMaterialsByLessonId(lessonId);
    }


    @Override
    public void submitAssignment(Long submissionId, Long studentId, Long assignmentId, String content) {
        Student student = getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        Course course = courseManagementService.getCourseById(assignment.getCourse().getId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        User user = userRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        enrollmentService.ensureStudentEnrollment(studentId, course.getId());

        submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .ifPresent(submission -> {
                    throw new IllegalStateException("Assignment already submitted by the student");
                });

        Submission submission = new Submission(student, assignment, content, LocalDateTime.now());

        submissionRepo.save(submission);

        activityLogRepo.save(new ActivityLog(user, "Submitted assignment: " + assignment.getTitle()));
    }

    public void ensureStudentAccessToLesson(Long studentId, Long lessonId) {
        boolean hasAccess = lessonRepo.existsByStudentIdAndLessonId(studentId, lessonId);
        if (!hasAccess) {
            throw new IllegalArgumentException("Student " + studentId + " has no access to lesson " + lessonId);
        }
    }
}