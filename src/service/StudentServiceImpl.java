package com.example.service;

import com.example.model.*;
import com.example.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {
    private final CourseManagementService courseManagementService;
    private final CourseEnrollmentService enrollmentService;
    private final StudentRepository studentRepo;
    private final UserRepository userRepo;
    private final SubmissionRepository submissionRepo;
    private final LessonRepository lessonRepo;
    private final GradeRepository gradeRepo;
    private final AssignmentRepository assignmentRepo;
    private final ActivityLogRepository activityLogRepo;


    public StudentServiceImpl(CourseManagementService courseManagementService, CourseEnrollmentService enrollmentService, StudentRepository studentRepo, UserRepository userRepo,
                              AssignmentRepository assignmentRepo, GradeRepository gradeRepo, SubmissionRepository submissionRepo, LessonRepository lessonRepo, ActivityLogRepository activityLogRepo) {
        this.courseManagementService = courseManagementService;
        this.enrollmentService = enrollmentService;
        this.studentRepo = studentRepo;
        this.userRepo = userRepo;
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.gradeRepo = gradeRepo;
        this.lessonRepo = lessonRepo;
        this.activityLogRepo = activityLogRepo;
    }

    @Override
    public Optional<Student> getStudentById(Long studentId) {
        studentRepo.ensureStudentExists(studentId);

        return studentRepo.findById(studentId);
    }

    @Override
    public List<Course> getEnrolledCourses(Long studentId) {
        studentRepo.ensureStudentExists(studentId);

        return studentRepo.findAllEnrolledCourses(studentId);
    }

    @Override
    public List<Submission> getSubmissionsByStudentId(Long studentId) {
        studentRepo.ensureStudentExists(studentId);

        return submissionRepo.findAllByStudentId(studentId);
    }

    @Override
    public Map<Assignment, Grade> getGradesForCourse(Long courseId, Long studentId) {
        studentRepo.ensureStudentExists(studentId);
        enrollmentService.ensureStudentEnrollment(courseId, studentId);

        return gradeRepo.findGradesByStudentIdForCourse(studentId, courseId);
    }

    @Override
    public Optional<Grade> getAssignmentGradeForStudent(Long assignmentId, Long studentId) {
        assignmentRepo.ensureAssignmentExists(assignmentId);
        studentRepo.ensureStudentExists(studentId);

        return gradeRepo.findByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    @Override
    public void enrollInCourse(Long studentId, Long courseId) {
        studentRepo.ensureStudentExists(studentId);

        Course course = courseManagementService.getCourseById(courseId).get();

        User user = userRepo.findById(studentId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        enrollmentService.enrollStudent(courseId, studentId);
        activityLogRepo.save(new ActivityLog(user, "Enrolled in course: " + course.getTitle()));
    }


    @Override
    public void dropCourse(Long studentId, Long courseId) {
        studentRepo.ensureStudentExists(studentId);

        Course course = courseManagementService.getCourseById(courseId).get();

        User user = userRepo.findById(studentId)
                        .orElseThrow(()-> new IllegalArgumentException("User not found"));
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
        studentRepo.ensureStudentExists(studentId);
        lessonRepo.ensureLessonExists(lessonId);
        lessonRepo.ensureStudentAccessToLesson(studentId, lessonId);

        User user = userRepo.findById(studentId)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

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
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        enrollmentService.ensureStudentEnrollment(studentId, course.getId());

        submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
                .ifPresent(submission -> {
                    throw new IllegalStateException("Assignment already submitted by the student");
                });

        Submission submission = new Submission(student, assignment, content, LocalDateTime.now());

        submissionRepo.save(submission);

        activityLogRepo.save(new ActivityLog(user, "Submitted assignment: " + assignment.getTitle()));
    }
}