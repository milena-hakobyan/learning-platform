package com.example.service;

import com.example.model.Course;
import com.example.model.Student;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaStudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {
    private final JpaCourseRepository courseRepo;
    private final JpaStudentRepository studentRepo;

    public CourseEnrollmentServiceImpl(JpaCourseRepository courseRepo, JpaStudentRepository studentRepo) {
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public List<Student> getEnrolledStudents(Long courseId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");

        if (!courseRepo.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        return courseRepo.findEnrolledStudents(courseId);
    }

    @Override
    public void ensureStudentEnrollment(Long studentId, Long courseId) {
        Objects.requireNonNull(studentId, "Student ID cannot be null");
        Objects.requireNonNull(courseId, "Course ID cannot be null");

        List<Student> students = courseRepo.findEnrolledStudents(courseId);
        boolean isEnrolled = students.stream().anyMatch(s -> s.getId().equals(studentId));

        if (!isEnrolled) {
            throw new IllegalArgumentException("Student is not enrolled in the course");
        }
    }

    @Override
    public void enrollStudent(Long courseId, Long studentId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        course.enrollStudent(student);
        courseRepo.save(course);
    }

    @Override
    public void unenrollStudent(Long courseId, Long studentId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        Objects.requireNonNull(studentId, "Student ID cannot be null");

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with ID: " + courseId));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));

        course.unenrollStudent(student);
        courseRepo.save(course);
    }
}
