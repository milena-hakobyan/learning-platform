package com.example.Service;

import com.example.Model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StudentService {
    Student getStudentById(Integer studentId);

    List<Course> getEnrolledCourses(Integer studentId);

    void enrollInCourse(Integer studentId, Integer courseId);

    void dropCourse(Integer studentId, Integer courseId);

    List<Submission> getSubmissionsByStudentId(Integer studentId);

    List<Course> browseAvailableCourses();

    List<Material> accessMaterials(Integer studentId, Integer courseId);

    Submission submitAssignment(Integer submissionId, Integer studentId, Integer assignmentId, String content);

    Map<Assignment, Grade> getGradesForCourse(Course course, Student student);

    Optional<Grade> findGradeForStudent(Assignment assignment, Student student);
}
