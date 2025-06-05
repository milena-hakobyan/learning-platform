package com.example.Repository;

import com.example.Model.Assignment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryAssignmentRepository implements AssignmentRepository {
    private final Map<String, Assignment> assignmentMap = new ConcurrentHashMap<>();

    @Override
    public void save(Assignment entity) {
        assignmentMap.put(entity.getAssignmentId(), entity);
    }

    @Override
    public void delete(String id) {
        assignmentMap.remove(id);
    }

    @Override
    public Assignment findById(String id) {
        return assignmentMap.get(id);
    }

    @Override
    public List<Assignment> findAll() {
        return assignmentMap.values().stream().toList();
    }

    @Override
    public List<Assignment> findByCourseId(String courseId) {
        return assignmentMap.values().stream()
                .filter(a -> a.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }


    @Override
    public List<Assignment> findByDueDateBefore(LocalDateTime date) {
        return assignmentMap.values().stream()
                .filter(a -> a.getDueDate().isBefore(date))
                .collect(Collectors.toList());
    }

    @Override
    public Assignment findByTitle(String title) {
        return assignmentMap.values().stream()
                .filter(assignment -> assignment.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);

    }
}
