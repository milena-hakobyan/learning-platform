package com.example.Service;

import com.example.Model.*;
import com.example.Repository.*;

import java.util.ArrayList;
import java.util.List;

public class InstructorServiceImpl implements InstructorService {
    private final UserService userService;
    private final CourseService courseService;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final AnnouncementRepository announcementRepo; // assume exists

    public InstructorServiceImpl(UserService userService, CourseService courseService,
                                 AssignmentRepository assignmentRepo,
                                 SubmissionRepository submissionRepo,
                                 AnnouncementRepository announcementRepo) {
        this.userService = userService;
        this.courseService = courseService;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.announcementRepo = announcementRepo;
    }

    @Override
    public List<Course> getCoursesCreated(String instructorId) {
        return courseService.getCoursesByInstructor(instructorId);
    }

    @Override
    public List<Lesson> getLessonsCreated(String instructorId) {
        List<Lesson> lessons = new ArrayList<>();
        for (Course course : getCoursesCreated(instructorId)) {
            lessons.addAll(course.getLessons());
        }
        return lessons;
    }

    @Override
    public List<Assignment> getAssignmentsCreated(String instructorId) {
        List<Assignment> assignments = new ArrayList<>();
        for (Course course : getCoursesCreated(instructorId)) {
            assignments.addAll(course.getAssignments());
        }
        return assignments;
    }

    @Override
    public void createCourse(Course course) {
        courseService.createCourse(course);
    }

    @Override
    public void createAssignment(Course course, Assignment assignment) {
        assignment.setCourseId(course.getCourseId());
        course.addAssignment(assignment);
        assignmentRepo.save(assignment);
        courseService.updateCourse(course);
    }

    @Override
    public void createLesson(Course course, Lesson lesson) {
        course.addLesson(lesson);
        courseService.updateCourse(course);
    }

    @Override
    public void uploadMaterial(Lesson lesson, Material material) {
        lesson.addMaterial(material);
    }

    @Override
    public void deleteMaterial(Lesson lesson, Material material) {
        lesson.removeMaterial(material);
    }

    @Override
    public void gradeAssignment(Assignment assignment, Student student, Grade grade) {
        List<Submission> submissions = submissionRepo.findByAssignmentId(assignment.getAssignmentId());
        Submission submission = submissions.stream()
                .filter(s -> s.getStudent().getUserId().equals(student.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Submission not found for student"));

        submission.setGrade(grade);
        submission.setStatus("graded");
        submissionRepo.save(submission);
    }

    @Override
    public void giveFeedback(Assignment assignment, Student student, String feedback) {
        List<Submission> submissions = submissionRepo.findByAssignmentId(assignment.getAssignmentId());
        Submission submission = submissions.stream()
                .filter(s -> s.getStudent().getUserId().equals(student.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Submission not found for student"));

        submission.setInstructorRemarks(feedback);
        submissionRepo.save(submission);
    }

    @Override
    public void sendAnnouncement(Course course, String title, String message) {
        Announcement announcement = new Announcement(title, message, course.getInstructorId(), course);
        course.postAnnouncement(announcement);
        announcementRepo.save(announcement);
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
    public User registerUser(String name, String username, String email, String rawPassword, Role role){
        return userService.registerUser(name, username,  email,  rawPassword, role);
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
