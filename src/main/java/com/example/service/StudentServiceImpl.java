package com.example.service;

import com.example.model.*;
import com.example.repository.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentServiceImpl implements StudentService {
    private final CourseManagementService courseManagementService;
    private final CourseEnrollmentService enrollmentService;
    private final StudentRepository studentRepo;
    private final SubmissionRepository submissionRepo;
    private final LessonRepository lessonRepo;
    private final GradeRepository gradeRepo;
    private final AssignmentRepository assignmentRepo;
    private final ActivityLogRepository activityLogRepo;


    public StudentServiceImpl(UserService userService, CourseManagementService courseManagementService, CourseEnrollmentService enrollmentService, StudentRepository studentRepo,
                              AssignmentRepository assignmentRepo, GradeRepository gradeRepo, SubmissionRepository submissionRepo, LessonRepository lessonRepo, ActivityLogRepository activityLogRepo) {
        this.courseManagementService = courseManagementService;
        this.enrollmentService = enrollmentService;
        this.studentRepo = studentRepo;
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.gradeRepo = gradeRepo;
        this.lessonRepo = lessonRepo;
        this.activityLogRepo = activityLogRepo;
    }

    @Override
    public Optional<Student> getStudentById(Integer studentId) {
        studentRepo.ensureStudentExists(studentId);

        return studentRepo.findById(studentId);
    }

    @Override
    public List<Course> getEnrolledCourses(Integer studentId) {
        studentRepo.ensureStudentExists(studentId);

        return studentRepo.findAllEnrolledCourses(studentId);
    }

    @Override
    public List<Submission> getSubmissionsByStudentId(Integer studentId) {
        studentRepo.ensureStudentExists(studentId);

        return submissionRepo.findAllByStudentId(studentId);
    }

    @Override
    public Map<Assignment, Grade> getGradesForCourse(Integer courseId, Integer studentId) {
        studentRepo.ensureStudentExists(studentId);
        enrollmentService.ensureStudentEnrollment(courseId, studentId);

        return gradeRepo.findGradesByStudentIdForCourse(studentId, courseId);
    }

    @Override
    public Optional<Grade> getAssignmentGradeForStudent(Integer assignmentId, Integer studentId) {
        assignmentRepo.ensureAssignmentExists(assignmentId);
        studentRepo.ensureStudentExists(studentId);

        return gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    @Override
    public void enrollInCourse(Integer studentId, Integer courseId) {
        studentRepo.ensureStudentExists(studentId);

        Course course = courseManagementService.getCourseById(courseId).get();

        enrollmentService.enrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(studentId, "Enrolled in course: " + course.getTitle()));
    }


    @Override
    public void dropCourse(Integer studentId, Integer courseId) {
        studentRepo.ensureStudentExists(studentId);

        Course course = courseManagementService.getCourseById(courseId).get();

        enrollmentService.ensureStudentEnrollment(studentId, courseId);

        enrollmentService.unenrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(studentId, "Dropped course: " + course.getTitle()));
    }


    @Override
    public List<Course> browseAvailableCourses() {
        return courseManagementService.getAllCourses();
    }

    @Override
    public List<Material> accessMaterials(Integer studentId, Integer lessonId) {
        studentRepo.ensureStudentExists(studentId);
        lessonRepo.ensureLessonExists(lessonId);
        lessonRepo.ensureStudentAccessToLesson(studentId, lessonId);

        activityLogRepo.save(new ActivityLog(studentId, "Accessed materials for lesson ID: " + lessonId));
        return lessonRepo.findAllMaterialsByLessonId(lessonId);
    }

    @Override
    public void submitAssignment(Integer submissionId, Integer studentId, Integer assignmentId, String content) {
        Student student = getStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        Course course = courseManagementService.getCourseById(assignment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        enrollmentService.ensureStudentEnrollment(studentId, course.getId());

        submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .ifPresent(submission -> {
                    throw new IllegalStateException("Assignment already submitted by the student");
                });

        Submission submission = new Submission(submissionId, studentId, assignmentId, content, LocalDateTime.now());

        submissionRepo.save(submission);
        activityLogRepo.save(new ActivityLog(studentId, "Submitted assignment: " + assignment.getTitle()));
    }
}