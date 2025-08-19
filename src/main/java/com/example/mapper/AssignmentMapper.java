package com.example.mapper;

import com.example.dto.assignment.AssignmentResponse;
import com.example.dto.assignment.CreateAssignmentRequest;
import com.example.dto.assignment.UpdateAssignmentRequest;
import com.example.model.Assignment;
import com.example.model.Course;
import org.springframework.stereotype.Component;

@Component
public class AssignmentMapper {

    public AssignmentResponse toDto(Assignment assignment) {
        AssignmentResponse response = new AssignmentResponse();
        response.setId(assignment.getId());
        response.setTitle(assignment.getTitle());
        response.setDescription(assignment.getDescription());
        response.setDueDate(assignment.getDueDate());
        response.setMaxScore(assignment.getMaxScore());

        if (assignment.getCourse() != null) {
            response.setCourseId(assignment.getCourse().getId());
            response.setCourseTitle(assignment.getCourse().getTitle());
        }

        return response;
    }

    public Assignment toEntity(CreateAssignmentRequest dto, Course course) {
        Assignment assignment = new Assignment();
        assignment.setTitle(dto.getTitle());
        assignment.setDescription(dto.getDescription());
        assignment.setMaxScore(dto.getMaxScore());
        assignment.setDueDate(dto.getDueDate());
        assignment.setCourse(course);
        return assignment;
    }

    public void updateEntity(UpdateAssignmentRequest dto, Assignment assignment) {
        if (dto.getTitle() != null) {
            assignment.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            assignment.setDescription(dto.getDescription());
        }
        if (dto.getDueDate() != null) {
            assignment.setDueDate(dto.getDueDate());
        }
        assignment.setMaxScore(dto.getMaxScore());
    }
}
