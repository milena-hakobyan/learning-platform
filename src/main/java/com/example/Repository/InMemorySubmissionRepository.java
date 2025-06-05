package com.example.Repository;

import com.example.Model.Submission;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemorySubmissionRepository implements SubmissionRepository {

    private final Map<String, Submission> submissions = new ConcurrentHashMap<>();

    @Override
    public void save(Submission entity) {
        submissions.put(entity.getSubmissionId(), entity);
    }

    @Override
    public void delete(String id) {
        submissions.remove(id);
    }

    @Override
    public Submission findById(String id) {
        return submissions.get(id);
    }

    @Override
    public List<Submission> findAll() {
        return List.copyOf(submissions.values());
    }

    @Override
    public List<Submission> findByStudentId(String studentId) {
        return submissions.values().stream().filter(s -> s.getStudent().getUserId().equals(studentId)).collect(Collectors.toList());
    }

    @Override
    public List<Submission> findByAssignmentId(String assignmentId) {
        return submissions.values().stream().filter(s -> s.getAssignment().getAssignmentId().equals(assignmentId)).collect(Collectors.toList());
    }

    @Override
    public Submission findByAssignmentIdAndStudentId(String assignmentId, String studentId) {
        return submissions.values().stream()
                .filter(s -> s.getAssignment().getAssignmentId().equals(assignmentId)
                        && s.getStudent().getUserId().equals(studentId))
                .findFirst()
                .orElse(null); // or throw an exception if preferred
    }


    @Override
    public List<Submission> findByStatus(String status) {
        return submissions.values().stream().filter(s -> s.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
    }
}
