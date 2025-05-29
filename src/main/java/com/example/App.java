package com.example;

import com.example.Repository.*;
import com.example.Service.*;
import com.example.Model.*;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        // Initialize repositories
        System.out.println("Initializing repositories...");
        CourseRepository courseRepo = new InMemoryCourseRepository();
        AssignmentRepository assignmentRepo = new InMemoryAssignmentRepository();
        UserRepository userRepo = new InMemoryUserRepository();
        SubmissionRepository submissionRepo = new InMemorySubmissionRepository();
        AnnouncementRepository announcementRepo = new InMemoryAnnouncementRepository();

        // Initialize services
        System.out.println("Initializing services...");
        CourseService courseService = new CourseServiceImpl(courseRepo, assignmentRepo, submissionRepo);
        UserService userService = new UserServiceImpl(userRepo);
        StudentService studentService = new StudentServiceImpl(userService, courseService, assignmentRepo, submissionRepo);
        InstructorService instructorService = new InstructorServiceImpl(userService, courseService, assignmentRepo, submissionRepo, announcementRepo);

        // Register instructors
        System.out.println("\nRegistering instructors...");
        User instructor = instructorService.registerUser("Suren Khachatryan","skhachat", "skhachat@aua.am", "password123", Role.INSTRUCTOR);
        User instructor2 = instructorService.registerUser("Hayk Nersisyan", "hnersisyan", "hnersisyan@aua.am", "password123", Role.INSTRUCTOR);
        User instructor3 = instructorService.registerUser("Apig Aramian", "aaramian", "aaramian@aua.am", "password123", Role.INSTRUCTOR);
        User instructor4 = instructorService.registerUser("Monika Stepanyan", "mstepanyan", "mstepanyan@aua.am", "password123", Role.INSTRUCTOR);

        // Creating valid courses
        System.out.println("\nCreating valid courses...\n");
        Course cs101 = new Course("CS239", "Quantum Computing", "Theory of Computing: Quantum edition", "Track course", instructor.getUserId());
        Course cs121 = new Course("CS121", "Data Structures", "Fundamentals for CS", "Core course", instructor3.getUserId());
        Course cs246 = new Course("CS246", "Artificial Intelligence", "ML/AI track for CS", "Track course", instructor4.getUserId());
        Course duplicateOfcs246 = new Course("CS246", "Artificial Intelligence", "ML/AI track for CS", "Track course", instructor4.getUserId());

        instructorService.createCourse(cs101);
        instructorService.createCourse(cs121);
        instructorService.createCourse(cs246);
        //trying to add a course with same courseId
        System.out.println("Adding a course with duplicate course id:");
        try {
            instructorService.createCourse(cs101);
        } catch (IllegalArgumentException e) {
            System.out.println("!!!Error creating course: " + e.getMessage());
        }
        // Register students
        System.out.println("\nRegistering students...");
        User student1 = studentService.registerUser("Milena Hakobyan", "mhakobyan", "milena_hakobyan@edu.aua.am", "youcantguessit", Role.STUDENT);
        User student2 = studentService.registerUser("Elen Balyan", "elenbalyan", "elen_balyan@edu.aua.am", "youcantguessit", Role.STUDENT);
        User student3 = studentService.registerUser("Mane Varosyan","mvarosyan", "mane_varosyan@edu.aua.am", "youcantguessit", Role.STUDENT);


        // Enroll students in the course
        System.out.println("\nEnrolling students into the courses: CS239 & CS121...");
        studentService.enrollInCourse(student1.getUserId(), "CS239");
        studentService.enrollInCourse(student1.getUserId(), "CS121");
        studentService.enrollInCourse(student2.getUserId(), "CS239");
        studentService.enrollInCourse(student3.getUserId(), "CS246");


        // Add lesson
        System.out.println("\nAdding a lesson to CS239...");
        Lesson lesson = new Lesson("Eigenvectors and eigenvalues", "1. Introduction to Eigenvectors\n2. Determinant and Trace");
        courseService.addLessonToCourse("CS239", lesson);

        // Add assignment
        System.out.println("\nAdding a quiz assignment to CS239...");
        Assignment assignmentQuantum = new Assignment("Quiz #1", "CS239", "Covering topics from week 1",
                LocalDateTime.of(2025, 2, 1, 23, 59), 100);

        System.out.println("\nAdding a hw assignment to CS121...");
        Assignment assignmentDS = new Assignment("HW #1", "CS121", "Covering topics from week 1-2",
                LocalDateTime.of(2025, 2, 8, 23, 59), 100);

        Assignment midtermAssignmentDS = new Assignment("midterm #1", "CS121", "Covering topics from week 1-5",
                LocalDateTime.of(2025, 3, 22, 20, 30), 100);
        courseService.addAssignmentToCourse("CS239", assignmentQuantum);
        courseService.addAssignmentToCourse("CS121", assignmentDS);
        courseService.addAssignmentToCourse("CS121", midtermAssignmentDS);



        // Post announcement by aaramian to CS121
        System.out.println("\nPosting announcement to CS121 by instructor aaramian...");
        instructorService.sendAnnouncement(
                cs121,
                "CS121B: Homework Posted",
                "Homework #1 has already been posted.!"
        );

        // Output list of instructors
        System.out.println("\nList of registered instructors:");
        userService.getUsersByRole(Role.INSTRUCTOR).forEach(System.out::println);
        System.out.println();

        // Output list of instructors
        System.out.println("\nList of registered students:");
        userService.getUsersByRole(Role.STUDENT).forEach(System.out::println);

        // Output list of courses
        System.out.println("\nList of available courses:");
        courseService.getAllCourses().forEach(System.out::println);

        //Deleting a course
        courseService.deleteCourse("CS239");
        System.out.println("\nList of available courses after deleting Quantum Computing:");
        courseService.getAllCourses().forEach(System.out::println);


        System.out.println("\nList of assignments for Data Structures before removing midterm1:");
        courseService.getCourseById("CS121").getAssignments().forEach(System.out::println);
        //Removing an assignment from a  course
        courseService.removeAssignmentFromCourse("CS121", assignmentDS.getAssignmentId());
        System.out.println("\nList of assignments for Data Structures after removing midterm1:");
        courseService.getCourseById("CS121").getAssignments().forEach(System.out::println);

        //Adding a submission(student) and grading the work(instructor)
        studentService.submitAssignment(student1.getUserId(), midtermAssignmentDS.getAssignmentId(), "...some code...");
        instructorService.gradeAssignment(midtermAssignmentDS, (Student) student1, new Grade(97,
                "Good job! You forgot to implement the remove method (-3)", LocalDateTime.now()));
        studentService.getSubmissions(student1.getUserId()).forEach(System.out::println);
    }
}