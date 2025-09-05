package com.example.service;

import com.example.dto.student.StudentResponse;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.StudentMapper;
import com.example.model.Course;
import com.example.model.Student;
import com.example.repository.JpaCourseRepository;
import com.example.repository.JpaStudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CourseEnrollmentServiceImpl implements CourseEnrollmentService {
    private final JpaCourseRepository courseRepo;
    private final JpaStudentRepository studentRepo;
    private final StudentMapper studentMapper;

    public CourseEnrollmentServiceImpl(JpaCourseRepository courseRepo, JpaStudentRepository studentRepo, StudentMapper studentMapper) {
        this.courseRepo = courseRepo;
        this.studentRepo = studentRepo;
        this.studentMapper = studentMapper;
    }

    @Override
    public Page<StudentResponse> getEnrolledStudents(Long courseId, Pageable pageable) {
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with ID: " + courseId);
        }

        return courseRepo.findEnrolledStudents(courseId, pageable)
                .map(studentMapper::toDto);
    }

    @Override
    public void ensureStudentEnrollment(Long studentId, Long courseId) {
        boolean isEnrolled = courseRepo.existsStudentEnrollment(courseId, studentId);
        if (!isEnrolled) {
            throw new ResourceNotFoundException("Student is not enrolled in the course");
        }
    }


    @Override
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        course.enrollStudent(student);
        courseRepo.save(course);
    }

    @Override
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        course.unenrollStudent(student);
        courseRepo.save(course);
    }
}