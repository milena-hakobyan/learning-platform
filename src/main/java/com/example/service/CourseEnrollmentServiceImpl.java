package com.example.service;

import com.example.model.Student;
import com.example.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {
    private final CourseRepository courseRepo;

    public CourseEnrollmentServiceImpl(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    @Override
    public List<Student> getEnrolledStudents(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return courseRepo.findEnrolledStudents(courseId);
    }

    @Override
    public void ensureStudentEnrollment(Integer studentId, Integer courseId) {
        if (!courseRepo.isStudentEnrolled(studentId, courseId))
            throw new IllegalArgumentException("Student is not enrolled in the course");
    }

    @Override
    public void enrollStudent(Integer courseId, Integer studentId) {
        courseRepo.enrollStudent(courseId, studentId);
    }

    @Override
    public void unenrollStudent(Integer courseId, Integer studentId) {
        courseRepo.unenrollStudent(courseId, studentId);
    }


}
