package com.example.Service;

import com.example.Model.*;
import com.example.Repository.AssignmentRepository;
import com.example.Repository.CourseRepository;
import com.example.Repository.SubmissionRepository;

import java.util.List;

public class CourseServiceImpl implements CourseService{

    private final CourseRepository courseRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;

    public CourseServiceImpl(CourseRepository courseRepo, AssignmentRepository assignmentRepo, SubmissionRepository submissionRepo) {
        this.courseRepo = courseRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
    }

    public void createCourse(Course course) {
        if (courseRepo.findById(course.getCourseId()) != null) {
            throw new IllegalArgumentException("Course with id '" + course.getCourseId() + "' already exists.");
        }
        if (courseRepo.findByTitle(course.getTitle()) != null) {
            throw new IllegalArgumentException("Course with title '" + course.getTitle() + "' already exists.");
        }
        courseRepo.save(course);
    }

    public void updateCourse(Course course) {
        if (courseRepo.findById(course.getCourseId()) == null) {
            throw new IllegalArgumentException("Course not found with id: " + course.getCourseId());
        }
        courseRepo.save(course);
    }

    public void deleteCourse(String courseId) {
        assignmentRepo.findByCourseId(courseId)
                .forEach(assignment -> {
                    submissionRepo.findByAssignmentId(assignment.getAssignmentId())
                            .forEach(submission -> submissionRepo.delete(submission.getSubmissionId()));
                    assignmentRepo.delete(assignment.getAssignmentId());
                });
        courseRepo.delete(courseId);
    }

    public void addAssignmentToCourse(String courseId, Assignment assignment) {
        Course course = courseRepo.findById(courseId);
        if (course == null) throw new IllegalArgumentException("Course not found");

        assignment.setCourseId(courseId);
        course.addAssignment(assignment);
        assignmentRepo.save(assignment);
    }

    public void removeAssignmentFromCourse(String courseId, String assignmentId) {
        Course course = courseRepo.findById(courseId);
        if (course == null) throw new IllegalArgumentException("Course not found");

        Assignment assignment = assignmentRepo.findById(assignmentId);
        if(assignment == null || !assignment.getCourseId().equals(courseId)){
            throw new IllegalArgumentException("Given course doesn't include an assignment with id " + assignmentId);
        }

        course.removeAssignment(assignment);
        submissionRepo.findByAssignmentId(assignmentId)
                        .forEach(submission -> submissionRepo.delete(submission.getSubmissionId()));

        assignmentRepo.delete(assignment.getAssignmentId());
    }

    public void addLessonToCourse(String courseId, Lesson lesson) {
        Course course = courseRepo.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found with ID: " + courseId);
        }

        course.addLesson(lesson);
        courseRepo.save(course); // Save the updated course with new lesson
    }


    public void removeLessonFromCourse(String courseId, String lessonId){
        Course course = courseRepo.findById(courseId);
        if (course == null) throw new IllegalArgumentException("Course not found");

        Lesson toBeDeleted = course.getLessons().stream()
                .filter(lesson -> lesson.getLessonId().equals(lessonId))
                .findFirst()
                .orElse(null);
        if(toBeDeleted == null)
            throw new IllegalArgumentException("Lesson not found");
        course.removeLesson(toBeDeleted);
    }

    @Override
    public void enrollStudent(String courseId, Student student) {
        Course course = courseRepo.findById(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Course not found");
        }

        course.enrollStudent(student);
        courseRepo.save(course);
    }

    public Course getCourseById(String courseId) {
        return courseRepo.findById(courseId);
    }

    public List<Course> getCoursesByInstructor(String instructorId) { //todo: add instructor repo!!!
        return courseRepo.findByInstructor(instructorId);
    }

    public List<Course> getCoursesByCategory(String category) {
        return courseRepo.findByCategory(category);
    }

    public List<Course> getCoursesByTags(List<String> tags) {
        return courseRepo.findByTag(tags);
    }

    public Course getCourseByTitle(String title) {
        return courseRepo.findByTitle(title);
    }

    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

}