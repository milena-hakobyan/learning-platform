package com.example.mapper;

import com.example.dto.course.CourseSummaryResponse;
import com.example.dto.instructor.InstructorResponse;
import com.example.dto.instructor.UpdateInstructorRequest;
import com.example.model.Course;
import com.example.model.Instructor;
import com.example.model.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstructorMapper {

    public InstructorResponse toDto(Instructor instructor) {
        InstructorResponse dto = new InstructorResponse();
        dto.setId(instructor.getId());
        dto.setUsername(instructor.getUser().getUsername());
        dto.setFirstName(instructor.getUser().getFirstName());
        dto.setLastName(instructor.getUser().getLastName());
        dto.setEmail(instructor.getUser().getEmail());
        dto.setBio(instructor.getBio());
        dto.setRating(instructor.getRating());
        dto.setVerified(instructor.isVerified());

        List<Course> courses = instructor.getCoursesCreated();
        dto.setTotalCoursesCreated(courses.size());

        dto.setCoursesCreated(
                courses.stream()
                        .map(course -> {
                            CourseSummaryResponse summary = new CourseSummaryResponse();
                            summary.setId(course.getId());
                            summary.setTitle(course.getTitle());
                            summary.setCategory(course.getCategory());
                            return summary;
                        })
                        .collect(Collectors.toList())
        );

        return dto;
    }

    public Instructor toEntity(User user, String bio) {
        Instructor instructor = new Instructor();
        instructor.setUser(user);
        instructor.setBio(bio);
        instructor.setVerified(false);
        instructor.setRating(0.0);
        return instructor;
    }

    public void updateEntity(UpdateInstructorRequest dto, Instructor instructor) {
        if (dto.getBio() != null) {
            instructor.setBio(dto.getBio());
        }
    }
}
