package com.example.mapper;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.model.Course;
import com.example.model.Instructor;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    private final LessonMapper lessonMapper;
    private final AssignmentMapper assignmentMapper;
    private final InstructorMapper instructorMapper;
    private final StudentMapper studentMapper;


    public CourseMapper(LessonMapper lessonMapper, AssignmentMapper assignmentMapper, InstructorMapper instructorMapper, StudentMapper studentMapper) {
        this.lessonMapper = lessonMapper;
        this.assignmentMapper = assignmentMapper;
        this.instructorMapper = instructorMapper;
        this.studentMapper = studentMapper;
    }

    public CourseResponse toDto(Course course) {
        CourseResponse dto = new CourseResponse();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setUrl(course.getUrl());

        dto.setInstructor(instructorMapper.toDto(course.getInstructor()));

        dto.setLessons(course.getLessons()
                .stream()
                .map(lessonMapper::toDto)
                .toList());

        dto.setAssignments(course.getAssignments()
                .stream()
                .map(assignmentMapper::toDto)
                .toList());

        dto.setEnrolledStudents(course.getEnrolledStudents()
                .stream()
                .map(studentMapper::toDto)
                .toList());


        return dto;
    }

    public Course toEntity(CreateCourseRequest request, Instructor instructor) {
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setUrl(request.getUrl());
        course.setInstructor(instructor);
        return course;
    }

    public void updateEntity(UpdateCourseRequest request, Course course) {
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getCategory() != null) course.setCategory(request.getCategory());
        if (request.getUrl() != null) course.setUrl(request.getUrl());
    }
}
