package com.example.mapper;

import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.model.Course;
import com.example.model.Student;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentResponse toDto(Student student) {
        StudentResponse dto = new StudentResponse();
        dto.setUserId(student.getUserId());
        dto.setProgressPercentage(student.getProgressPercentage());
        dto.setCompletedCourses(student.getCompletedCourses());
        dto.setCurrentCourses(student.getCurrentCourses());

        List<Long> enrolledCourseIds = student.getEnrolledCourses().stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        dto.setEnrolledCourseIds(enrolledCourseIds);

        return dto;
    }

    public Student toEntity(Long userId) {
        return new Student(userId);
    }

    public void updateEntity(UpdateStudentRequest dto, Student student) {
        if (dto.getProgressPercentage() != null) {
            student.setProgressPercentage(dto.getProgressPercentage());
        }
        if (dto.getCompletedCourses() != null) {
            student.setCompletedCourses(dto.getCompletedCourses());
        }
        if (dto.getCurrentCourses() != null) {
            student.setCurrentCourses(dto.getCurrentCourses());
        }
    }
}
