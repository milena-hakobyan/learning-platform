package com.example.dto.course;

import com.example.controller.AssignmentController;
import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.instructor.InstructorResponse;
import com.example.dto.lesson.LessonResponse;
import com.example.dto.student.StudentResponse;
import com.example.model.Assignment;
import com.example.model.Instructor;
import com.example.model.Lesson;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String url;

    private InstructorResponse instructor;

    private List<LessonResponse> lessons;
    private List<AssignmentResponse> assignments;
    private List<StudentResponse> enrolledStudents;
}
