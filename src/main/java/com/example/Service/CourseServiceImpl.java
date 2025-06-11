package com.example.Service;

import com.example.Model.*;
import com.example.Repository.*;
import com.example.Utils.InputValidationUtils;

import java.util.List;
import java.util.Optional;

public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepo;
    private final LessonRepository lessonRepo;
    private final AssignmentRepository assignmentRepo;
    private final SubmissionRepository submissionRepo;
    private final AnnouncementRepository announcementRepo;
    private final UserService userService;

    public CourseServiceImpl(CourseRepository courseRepo, LessonRepository lessonRepo, AssignmentRepository assignmentRepo,
                             SubmissionRepository submissionRepo, AnnouncementRepository announcementRepo, UserService userService) {
        this.courseRepo = courseRepo;
        this.lessonRepo = lessonRepo;
        this.assignmentRepo = assignmentRepo;
        this.submissionRepo = submissionRepo;
        this.announcementRepo = announcementRepo;
        this.userService = userService;
    }

    @Override
    public void createCourse(Course course) {
        InputValidationUtils.requireNonNull(course, "Course cannot be null");

        if (courseRepo.findById(course.getCourseId()).isPresent()) {
            throw new IllegalArgumentException("Course with id '" + course.getCourseId() + "' already exists.");
        }
        if (courseRepo.findByTitle(course.getTitle()).isPresent()) {
            throw new IllegalArgumentException("Course with title '" + course.getTitle() + "' already exists.");
        }
        courseRepo.save(course);
    }

    @Override
    public void updateCourse(Course course) {
        InputValidationUtils.requireNonNull(course, "Course cannot be null");
        InputValidationUtils.requireCourseExists(course.getCourseId(), courseRepo);

        courseRepo.save(course);
    }

    @Override
    public void deleteCourse(Integer courseId) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);

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
        Course course = InputValidationUtils.requireCourseExists(courseId, courseRepo);

        if (assignment.getCourseId() != null && !assignment.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Assignment already belongs to a different course.");
        }
        course.addAssignment(assignment);
        assignmentRepo.save(assignment);
        courseRepo.update(course);
    }

    @Override
    public void removeAssignmentFromCourse(Integer courseId, Integer assignmentId) {
        Course course = InputValidationUtils.requireCourseExists(courseId, courseRepo);

        Assignment assignment = assignmentRepo.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (!assignment.getCourseId().equals(courseId)) {
            throw new IllegalArgumentException("Given course doesn't include an assignment with id " + assignmentId);
        }

        course.removeAssignment(assignment);
        submissionRepo.findByAssignmentId(assignmentId)
                .forEach(submission -> submissionRepo.delete(submission.getSubmissionId()));

        assignmentRepo.delete(assignment.getAssignmentId());
        courseRepo.update(course);
    }

    @Override
    public void addLessonToCourse(Integer courseId, Lesson lesson) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);
        InputValidationUtils.requireNonNull(lesson, "Lesson cannot be null");

        Course course = InputValidationUtils.requireCourseExists(courseId, courseRepo);

        course.addLesson(lesson);
        courseRepo.update(course);
    }

    @Override
    public void removeLessonFromCourse(Integer courseId, Integer lessonId) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);
        InputValidationUtils.requireLessonExists(lessonId, lessonRepo);

        Course course = InputValidationUtils.requireCourseExists(courseId, courseRepo);

        Lesson toBeDeleted = course.getLessons().stream()
                .filter(lesson -> lesson.getLessonId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found in course"));

        course.removeLesson(toBeDeleted);
        lessonRepo.delete(lessonId);
        courseRepo.update(course);
    }

    @Override
    public void addMaterialToLesson(Integer lessonId, Material material) {
        InputValidationUtils.requireLessonExists(lessonId, lessonRepo);
        InputValidationUtils.requireNonNull(material, "Material cannot be null");

        lessonRepo.addMaterial(lessonId, material);
    }

    @Override
    public void removeMaterialFromLesson(Integer lessonId, Integer materialId) {
        InputValidationUtils.requireLessonExists(lessonId, lessonRepo);
        InputValidationUtils.requireNonNull(materialId, "Material ID cannot be null");

        lessonRepo.removeMaterial(lessonId, materialId);
    }

    @Override
    public void addMaterialToAssignment(Integer assignmentId, Material material) {
        InputValidationUtils.requireAssignmentExists(assignmentId, assignmentRepo);
        InputValidationUtils.requireNonNull(material, "Material cannot be null");

        assignmentRepo.addMaterial(assignmentId, material);
    }

    @Override
    public void removeMaterialFromAssignment(Integer assignmentId, Integer materialId) {
        InputValidationUtils.requireAssignmentExists(assignmentId, assignmentRepo);
        InputValidationUtils.requireNonNull(materialId, "Material ID cannot be null");

        assignmentRepo.removeMaterial(assignmentId, materialId);
    }

    @Override
    public void enrollStudent(Integer courseId, Student student) {
        Course course = InputValidationUtils.requireCourseExists(courseId, courseRepo);
        InputValidationUtils.requireNonNull(student, "Student cannot be null");

        if (userService.getUserById(student.getUserId()).isEmpty()) {
            throw new IllegalArgumentException("User with id '" + student.getUserId() + "' doesn't exist.");
        }

        course.enrollStudent(student);
        courseRepo.enrollStudent(courseId, student);
    }

    @Override
    public List<Lesson> getLessonsForCourse(Integer courseId) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);

        return lessonRepo.findByCourseId(courseId);
    }

    @Override
    public List<Assignment> getAssignmentsForCourse(Integer courseId) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);

        return assignmentRepo.findByCourseId(courseId);
    }

    @Override
    public List<Announcement> getAnnouncementsForCourse(Integer courseId) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);

        return announcementRepo.findByCourseId(courseId);
    }

    @Override
    public Optional<Course> getCourseById(Integer courseId) {
        InputValidationUtils.requireNonNull(courseId, "Course Id cannot be null");

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
        InputValidationUtils.requireNonNull(instructorId, "InstructorId cannot be null");

        return courseRepo.findByInstructor(instructorId);
    }

    @Override
    public List<Course> getCoursesByCategory(String category) {
        InputValidationUtils.requireNonNull(category, "Category cannot be null");

        return courseRepo.findByCategory(category);
    }

    @Override
    public Optional<Course> getCourseByTitle(String title) {
        InputValidationUtils.requireNonNull(title, "Title cannot be null");

        return courseRepo.findByTitle(title);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepo.findAll();
    }

    @Override
    public List<Student> getEnrolledStudents(Integer courseId) {
        InputValidationUtils.requireCourseExists(courseId, courseRepo);

        return courseRepo.findEnrolledStudents(courseId);
    }
}