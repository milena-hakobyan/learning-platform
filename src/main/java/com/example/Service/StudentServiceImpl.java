package com.example.Service;

import com.example.Model.*;
import com.example.Repository.*;
import com.example.Utils.InputValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentServiceImpl implements StudentService {
    private final UserService userService;
    private final CourseService courseService;
    private final StudentRepository studentRepo;
    private final SubmissionRepository submissionRepo;
    private final LessonRepository lessonRepo;
    private final GradeRepository gradeRepo;
    private final AssignmentRepository assignmentRepo;


    public StudentServiceImpl(UserService userService, CourseService courseService, StudentRepository studentRepo,
                              AssignmentRepository assignmentRepo, GradeRepository gradeRepo, SubmissionRepository submissionRepo, LessonRepository lessonRepo) {
        this.courseService = courseService;
        this.studentRepo = studentRepo;
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.gradeRepo = gradeRepo;
        this.userService = userService;
        this.lessonRepo = lessonRepo;
    }

    @Override
    public Student getStudentById(Integer studentId) {
        InputValidationUtils.validateStudentExists(studentId, userService);
        return studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No student with given id"));
    }

    @Override
    public List<Course> getEnrolledCourses(Integer studentId) {
        Student student = getStudentById(studentId);
        return student.getEnrolledCourses();
    }

    @Override
    public void enrollInCourse(Integer studentId, Integer courseId) {
        Student student = getStudentById(studentId);
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        student.enroll(course);
        course.enrollStudent(student);
        courseService.enrollStudent(courseId, student);
    }


    @Override
    public void dropCourse(Integer studentId, Integer courseId) {
        Student student = getStudentById(studentId);
        Course course = courseService.getCourseById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (student.getEnrolledCourses().contains(course)) {
            student.getEnrolledCourses().remove(course);
            userService.updateUser(student);
        }
    }

    @Override
    public List<Submission> getSubmissionsByStudentId(Integer studentId) {
        InputValidationUtils.validateStudentExists(studentId, userService);

        return submissionRepo.findByStudentId(studentId);
    }

    @Override
    public List<Course> browseAvailableCourses() {
        return courseService.getAllCourses();
    }

    @Override
    public List<Material> accessMaterials(Integer studentId, Integer lessonId) {
        InputValidationUtils.validateStudentExists(studentId, userService);
        InputValidationUtils.requireLessonExists(lessonId, lessonRepo);

        if (!lessonRepo.verifyStudentAccessToLesson(studentId, lessonId)){
            throw new IllegalArgumentException("Student has no access to the course");
        }

        return lessonRepo.findMaterialsByLessonId(lessonId);
    }

    @Override
    public Submission submitAssignment(Integer submissionId, Integer studentId, Integer assignmentId, String content) {
        Student student = getStudentById(studentId);
        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        Course course = courseService.getCourseById(assignment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (!course.getEnrolledStudents().contains(student)) {
            throw new IllegalArgumentException("Student not enrolled in the course");
        }

        Optional<Submission> existing = submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId);

        if (existing.isPresent()) {
            throw new IllegalStateException("Assignment already submitted by the student");
        }

        Submission submission = new Submission(submissionId, studentId, assignmentId, content, LocalDateTime.now());
        student.getSubmissions().add(submission);
        return submissionRepo.save(submission);
    }

     @Override
    public Map<Assignment, Grade> getGradesForCourse(Course course, Student student) {
         InputValidationUtils.requireNonNull(course, "Course cannot be null");

         courseService.getCourseById(course.getCourseId())
                 .orElseThrow(() -> new IllegalArgumentException("Course with ID " + course.getCourseId() + " does not exist."));

         InputValidationUtils.validateStudentExists(student.getUserId(), userService);

        return gradeRepo.findGradesByStudentIdForCourse(student.getUserId(), course.getCourseId());
    }

    @Override
    public Optional<Grade> findGradeForStudent(Assignment assignment, Student student) {
        InputValidationUtils.requireAssignmentExists(assignment.getAssignmentId(), assignmentRepo);
        InputValidationUtils.validateStudentExists(student.getUserId(), userService);

        return gradeRepo.findByAssignmentIdAndStudentId(assignment.getAssignmentId(), student.getUserId());
    }
}