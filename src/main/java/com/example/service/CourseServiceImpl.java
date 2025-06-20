package com.example.service;

import com.example.model.*;
import com.example.repository.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepo;
    private final LessonRepository lessonRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final AnnouncementRepository announcementRepo;
    private final StudentRepository studentRepo;

    public CourseServiceImpl(CourseRepository courseRepo, LessonRepository lessonRepo, AssignmentRepository assignmentRepo,
                             SubmissionRepository submissionRepo, AnnouncementRepository announcementRepo, StudentRepository studentRepo) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.announcementRepo = announcementRepo;
        this.studentRepo = studentRepo;
    }

    @Override
    public void createCourse(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        courseRepo.ensureCourseExists(course.getCourseId());

        if (courseRepo.findByTitle(course.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Course with title '" + course.getTitle() + "' already exists.");
        }
        courseRepo.save(course);
    }

    @Override
    public void updateCourse(Course course) {
        Objects.requireNonNull(course, "Course cannot be null");
        courseRepo.ensureCourseExists(course.getCourseId());

        courseRepo.save(course);
    }

    @Override
    public void deleteCourse(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        assignmentRepo.findByCourseId(courseId)
                .forEach(assignment -> {
                    submissionRepo.findByAssignmentId(assignment.getAssignmentId())
                            .forEach(submission -> submissionRepo.delete(submission.getSubmissionId()));
                    assignmentRepo.delete(assignment.getAssignmentId());
                });
        courseRepo.delete(courseId);
    }

    @Override
    public void addAssignmentToCourse(Integer courseId, Assignment assignment) {
        courseRepo.ensureCourseExists(courseId);

        if (assignment.getCourseId() != null && !assignment.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Assignment already belongs to a different course.");
        }
        assignmentRepo.save(assignment);
    }

    @Override
    public void removeAssignmentFromCourse(Integer courseId, Integer assignmentId) {
        courseRepo.ensureCourseExists(courseId);

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Given course doesn't include an assignment with id " + assignmentId);
        }

        submissionRepo.findByAssignmentId(assignmentId)
                .forEach(submission -> submissionRepo.delete(submission.getSubmissionId()));

        assignmentRepo.delete(assignmentId);
    }

    @Override
    public void addLessonToCourse(Integer courseId, Lesson lesson) {
        courseRepo.ensureCourseExists(courseId);
        Objects.requireNonNull(lesson, "Lesson cannot be null");

        lesson.setCourseId(courseId);

        lessonRepo.save(lesson);
    }

    @Override
    public void removeLessonFromCourse(Integer courseId, Integer lessonId) {
        courseRepo.ensureCourseExists(courseId);
        lessonRepo.ensureLessonExists(lessonId);

        Course course = courseRepo.findById(courseId).get();

        boolean lessonBelongsToCourse = course.getLessons().stream()
                .anyMatch(lesson -> lesson.getLessonId().equals(lessonId));

        if (!lessonBelongsToCourse) {
            throw new IllegalArgumentException("Lesson does not belong to the given course");
        }

        lessonRepo.delete(lessonId);
    }

    @Override
    public void addMaterialToLesson(Integer lessonId, Material material) {
        lessonRepo.ensureLessonExists(lessonId);
        Objects.requireNonNull(material, "Material cannot be null");

        lessonRepo.addMaterial(lessonId, material);
    }

    @Override
    public void removeMaterialFromLesson(Integer lessonId, Integer materialId) {
        lessonRepo.ensureLessonExists(lessonId);
        Objects.requireNonNull(materialId, "Material ID cannot be null");

        lessonRepo.removeMaterial(lessonId, materialId);
    }

    @Override
    public void addMaterialToAssignment(Integer assignmentId, Material material) {
        assignmentRepo.ensureAssignmentExists(assignmentId);
        Objects.requireNonNull(material, "Material cannot be null");

        assignmentRepo.addMaterial(assignmentId, material);
    }

    @Override
    public void removeMaterialFromAssignment(Integer assignmentId, Integer materialId) {
        assignmentRepo.ensureAssignmentExists(assignmentId);
        Objects.requireNonNull(materialId, "Material ID cannot be null");

        assignmentRepo.removeMaterial(assignmentId, materialId);
    }

    @Override
    public void enrollStudent(Integer courseId, Integer studentId) {
        courseRepo.enrollStudent(courseId, studentId);
    }

    @Override
    public void unenrollStudent(Integer courseId, Integer studentId) {
        courseRepo.unenrollStudent(courseId, studentId);
    }

    @Override
    public List<Lesson> getLessonsForCourse(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return lessonRepo.findByCourseId(courseId);
    }

    @Override
    public List<Assignment> getAssignmentsForCourse(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return assignmentRepo.findByCourseId(courseId);
    }

    @Override
    public List<Announcement> getAnnouncementsForCourse(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return announcementRepo.findByCourseId(courseId);
    }

    @Override
    public Optional<Course> getCourseById(Integer courseId) {
        Objects.requireNonNull(courseId, "Course Id cannot be null");

        return courseRepo.findById(courseId);
    }

    @Override
    public Optional<Course> getByIdWithLessons(Integer courseId) {
        Optional<Course> course = getCourseById(courseId);
        if (course.isPresent()) {
            List<Lesson> lessons = lessonRepo.findByCourseId(courseId);
            course.get().setLessons(lessons);
        }
        return course;
    }

    @Override
    public List<Course> getCoursesByInstructor(Integer instructorId) {
        Objects.requireNonNull(instructorId, "InstructorId cannot be null");

        return courseRepo.findByInstructor(instructorId);
    }

    @Override
    public List<Course> getCoursesByCategory(String category) {
        Objects.requireNonNull(category, "Category cannot be null");

        return courseRepo.findByCategory(category);
    }

    @Override
    public Optional<Course> getCourseByTitle(String title) {
        Objects.requireNonNull(title, "Title cannot be null");

        return courseRepo.findByTitle(title);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public List<Student> getEnrolledStudents(Integer courseId) {
        courseRepo.ensureCourseExists(courseId);

        return courseRepo.findEnrolledStudents(courseId);
    }

    @Override
    public void ensureStudentEnrollment(Integer studentId, Integer courseId) {
        if (!courseRepo.isStudentEnrolled(studentId, courseId))
            throw new IllegalArgumentException("Student is not enrolled in the course");
    }
}