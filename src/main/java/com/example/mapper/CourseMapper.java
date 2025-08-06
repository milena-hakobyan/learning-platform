package com.example.mapper;

import com.example.dto.course.CourseResponse;
import com.example.dto.course.CreateCourseRequest;
import com.example.dto.course.UpdateCourseRequest;
import com.example.model.Course;
import com.example.model.Instructor;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

    public CourseResponse toDto(Course course) {
        CourseResponse dto = new CourseResponse();
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCategory(course.getCategory());
        dto.setUrl(course.getUrl());

        String fullName = course.getInstructor().getUser().getFirstName() + " " +
                course.getInstructor().getUser().getLastName();
        dto.setInstructorName(fullName);

        dto.setLessonsCount(course.getLessons().size());
        dto.setAssignmentsCount(course.getAssignments().size());
        dto.setEnrolledStudentCount(course.getEnrolledStudents().size());

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
        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            course.setCategory(request.getCategory());
        }
        if (request.getUrl() != null) {
            course.setUrl(request.getUrl());
        }
    }
}
