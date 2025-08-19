package com.example.mapper;

import com.example.dto.student.RegisterStudentRequest;
import com.example.dto.student.StudentResponse;
import com.example.dto.student.UpdateStudentRequest;
import com.example.model.Course;
import com.example.model.Student;
import com.example.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    public StudentResponse toDto(Student student) {
        StudentResponse dto = new StudentResponse();
        dto.setId(student.getId());
        dto.setUsername(student.getUser().getUsername());
        dto.setFirstName(student.getUser().getFirstName());
        dto.setLastName(student.getUser().getLastName());
        dto.setEmail(student.getUser().getEmail());
        dto.setProgressPercentage(student.getProgressPercentage());
        dto.setCompletedCourses(student.getCompletedCourses());
        dto.setCurrentCourses(student.getCurrentCourses());

        List<Long> enrolledCourseIds = student.getEnrolledCourses().stream()
                .map(Course::getId)
                .collect(Collectors.toList());
        dto.setEnrolledCourseIds(enrolledCourseIds);

        return dto;
    }

    public Student toEntity(User user) {
        return new Student(user);
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
