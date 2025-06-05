package com.example.Repository;

import com.example.Model.Course;
import com.example.Model.Material;
import com.example.Model.Student;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCourseRepository implements CourseRepository {
    private final Map<String, Course> courses = new ConcurrentHashMap<>();

    @Override
    public void save(Course entity) {
        courses.put(entity.getCourseId(), entity);
    }

    @Override
    public void delete(String id) {
        courses.remove(id);
    }

    @Override
    public Optional<Course> findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }
        return Optional.ofNullable(courses.get(id));
    }


    @Override
    public List<Course> findAll() {
        return courses.values().stream().toList();
    }

    @Override
    public Optional<Course> findByTitle(String title) {
        return courses.values().stream()
                .filter(course -> course.getTitle().equalsIgnoreCase(title))
                .findFirst();
    }

    @Override
    public List<Course> findByInstructor(String instructorId) {
        return courses.values().stream()
                .filter(course -> course.getInstructorId().equals(instructorId))
                .toList();
    }

    @Override
    public List<Course> findByCategory(String category) {
        return courses.values().stream()
                .filter(course -> course.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    @Override
    public List<Course> findByTag(List<String> tags) {
        return courses.values().stream()
                .filter(course -> course.getTags().stream().anyMatch(tags::contains))
                .toList();
    }

    @Override
    public void enrollStudent(String courseId, Student student) {
        Course course = courses.get(courseId);
        if (course != null) {
            course.enrollStudent(student);
        }
    }

}