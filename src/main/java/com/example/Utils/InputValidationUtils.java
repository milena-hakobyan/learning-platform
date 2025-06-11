package com.example.Utils;

import java.util.Optional;

import com.example.Model.Assignment;
import com.example.Model.Course;
import com.example.Model.Lesson;
import com.example.Model.Submission;
import com.example.Repository.*;
import com.example.Service.CourseService;
import com.example.Service.UserService;

public class InputValidationUtils {

    private InputValidationUtils() { }

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
        return obj;
    }

    public static Course requireCourseExists(Integer courseId, CourseRepository repo) {
        requireNonNull(courseId, "Course Id cannot be null");
        return repo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course with id '" + courseId + "' doesn't exist."));
    }

    public static Lesson requireLessonExists(Integer lessonId, LessonRepository repo) {
        requireNonNull(lessonId, "Lesson Id cannot be null");
        return repo.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson with id '" + lessonId + "' doesn't exist."));
    }

    public static Assignment requireAssignmentExists(Integer assignmentId, AssignmentRepository repo) {
        requireNonNull(assignmentId, "Assignment Id cannot be null");
        return repo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment with id '" + assignmentId + "' doesn't exist."));
    }

    public static Submission requireSubmissionExists(Integer submissionId, SubmissionRepository repo) {
        requireNonNull(submissionId, "Submission Id cannot be null");
        return repo.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission with id '" + submissionId + "' doesn't exist."));
    }

    public static void validateInstructorExists(Integer instructorId, UserService userService) {
        InputValidationUtils.requireNonNull(instructorId, "Instructor Id cannot be null");

        if (userService.getUserById(instructorId).isEmpty()) {
            throw new IllegalArgumentException("Instructor is not in the system");
        }
    }

    public static void validateStudentExists(Integer studentId, UserService userService) {
        InputValidationUtils.requireNonNull(studentId, "Student Id cannot be null");

        if (userService.getUserById(studentId).isEmpty()) {
            throw new IllegalArgumentException("Student is not in the system");
        }
    }

}
