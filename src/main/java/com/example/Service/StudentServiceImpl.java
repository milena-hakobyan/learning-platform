package com.example.Service;

import com.example.Model.*;
import com.example.Repository.AssignmentRepository;
import com.example.Repository.CourseRepository;
import com.example.Repository.SubmissionRepository;
import com.example.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentServiceImpl implements StudentService {
    private final UserService userService;
    private final CourseService courseService;
    private final SubmissionRepository submissionRepo;
    private final AssignmentRepository assignmentRepo;


    public StudentServiceImpl(UserService userService, CourseService courseService,
                              AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo) {
        this.courseService = courseService;
        this.submissionRepo = submissionRepo;
        this.assignmentRepo = assignmentRepo;
        this.userService = userService;
    }

    private Student getStudentById(String studentId) {
        User user = userService.getUserById(studentId);
        if (user == null) {
            throw new IllegalArgumentException("No student with given id");
        }
        if (!user.getRole().equals(Role.STUDENT)) {
            System.out.println(user.getClass());
            throw new IllegalArgumentException("User with id " + studentId + " is not a student");
        }
        return (Student) user;
    }

    @Override
    public List<Course> getEnrolledCourses(String studentId) {
        Student student = getStudentById(studentId);
        return student.getEnrolledCourses();
    }

    @Override
    public void enrollInCourse(String studentId, String courseId) {
        Student student = getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);

        courseService.enrollStudent(courseId, student);
        student.enroll(course);
    }


    @Override
    public void dropCourse(String studentId, String courseId) {
        Student student = getStudentById(studentId);
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }
        if (student.getEnrolledCourses().contains(course)) {
            student.getEnrolledCourses().remove(course);
            userService.updateUser(student);  // persist changes
        }
    }

    @Override
    public List<Submission> getSubmissions(String studentId) {
        Student student = getStudentById(studentId);
        return student.getSubmissions();
    }

    @Override
    public List<Course> browseCourses() {
        return courseService.getAllCourses();
    }

    @Override
    public List<Lesson> accessMaterials(String studentId, String courseId) {
        Student student = getStudentById(studentId);

        return getEnrolledCourses(studentId).stream()
                .filter(course -> course.getCourseId().equals(courseId))
                .findFirst()
                .map(course -> course.getLessons())
                .orElseThrow(() -> new IllegalArgumentException("Student not enrolled in the course"));
    }

    @Override
    public void submitAssignment(String studentId, String assignmentId, String content) {
        Student student = getStudentById(studentId); // assumes you have a helper method for casting + lookup
        Assignment assignment = assignmentRepo.findById(assignmentId);
        if (assignment == null) throw new IllegalArgumentException("Assignment not found");

        Course course = courseService.getCourseById(assignment.getCourseId());
        if (course == null) throw new IllegalArgumentException("Course not found");

        if (!course.getEnrolledStudents().contains(student)) {
            throw new IllegalArgumentException("Student not enrolled in the course");
        }

        Optional<Submission> existing = Optional.ofNullable(
                submissionRepo.findByAssignmentIdAndStudentId(assignmentId, studentId)
        );

        if (existing.isPresent()) {
            throw new IllegalStateException("Assignment already submitted by the student");
        }

        Submission submission = new Submission(student, assignment, content, LocalDateTime.now());
        student.getSubmissions().add(submission);
        submissionRepo.save(submission);
    }



    @Override
    public Map<Course, Map<Assignment, Grade>> viewGrades(String studentId) {
        Student student = getStudentById(studentId);

        Map<Course, Map<Assignment, Grade>> grades = new HashMap<>();

        for (Course course : getEnrolledCourses(studentId)) {
            Map<Assignment, Grade> assignmentGrades = new HashMap<>();

            for (Assignment assignment : course.getAssignments()) {
                List<Submission> submissions = submissionRepo.findByAssignmentId(assignment.getAssignmentId());

                for (Submission submission : submissions) {
                    if (submission.getStudent().getUserId().equals(studentId)) {
                        assignmentGrades.put(assignment, submission.getGrade());
                    }
                }
            }

            if (!assignmentGrades.isEmpty()) {
                grades.put(course, assignmentGrades);
            }
        }

        return grades;
    }

    public void submitAssignment(String studentId, Submission submission) {
        User user = userService.getUserById(studentId);
        if (user instanceof Student) {
            Student student = (Student) user;
            student.getSubmissions().add(submission);
            submissionRepo.save(submission);
        } else {
            throw new IllegalArgumentException("User is not a student.");
        }
    }



    @Override
    public User getUserById(String id) {
        return userService.getUserById(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public User getUserByUserName(String userName) {
        return userService.getUserByUserName(userName);
    }



    @Override
    public List<User> getUsersByRole(Role role) {
        return userService.getUsersByRole(role);
    }

    @Override
    public User registerUser(String name, String username, String email, String rawPassword, Role role) {
        return userService.registerUser(name, username, email, rawPassword, role);
    }

    @Override
    public User login(String email, String password) {
        return userService.login(email, password);
    }

    @Override
    public void updateUser(User user) {
        userService.updateUser(user);
    }

    @Override
    public void deleteUser(String id) {
        userService.deleteUser(id);
    }
}
