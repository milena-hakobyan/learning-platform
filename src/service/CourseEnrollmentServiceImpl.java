package com.example.service;

import com.example.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {
    private final CourseRepository courseRepo;

    public CourseEnrollmentServiceImpl(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    @Override
    public List<Student> getEnrolledStudents(Long courseId) {
        courseRepo.ensureCourseExists(courseId);

        return courseRepo.findEnrolledStudents(courseId);
    }

    @Override
    public void ensureStudentEnrollment(Long studentId, Long courseId) {
        if (!courseRepo.isStudentEnrolled(studentId, courseId))
            throw new IllegalArgumentException("Student is not enrolled in the course");
    }

    @Override
    public void enrollStudent(Long courseId, Long studentId) {
        courseRepo.enrollStudent(courseId, studentId);
    }

    @Override
    public void unenrollStudent(Long courseId, Long studentId) {
        courseRepo.unenrollStudent(courseId, studentId);
    }
}