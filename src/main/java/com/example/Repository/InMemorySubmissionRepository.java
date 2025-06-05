package com.example.Repository;

import com.example.Model.Submission;
import com.example.Model.SubmissionStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public Optional<Submission> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return Optional.ofNullable(submissions.get(id));
    }

    @Override
    public List<Submission> findAll() {
        return List.copyOf(submissions.values());
    }

    @Override
    public List<Submission> findByStudentId(String studentId) {
        return submissions.values().stream()
                .filter(submission -> {
                    String submissionStudentId = submission.getStudent().getUserId();
                    return submissionStudentId.equals(studentId);
                })
                .toList();
    }

    @Override
    public List<Submission> findByAssignmentId(String assignmentId) {
        return submissions.values().stream().filter(s -> s.getAssignment().getAssignmentId().equals(assignmentId)).collect(Collectors.toList());
    }

    @Override
    public Optional<Submission> findByAssignmentIdAndStudentId(String assignmentId, String studentId) {
        if (assignmentId == null) {
            throw new IllegalArgumentException("Assignment ID cannot be null");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Student ID cannot be null");
        }

        return submissions.values().stream()
                .filter(s -> {
                    return s.getAssignment() != null
                            && assignmentId.equals(s.getAssignment().getAssignmentId())
                            && s.getStudent() != null
                            && studentId.equals(s.getStudent().getUserId());
                })
                .findFirst();
    }



    @Override
    public List<Submission> findByStatus(SubmissionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return submissions.values().stream()
                .filter(s -> s.getStatus() == status)
                .collect(Collectors.toList());
    }

}
