package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;
import com.example.utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JdbcCourseRepo implements CourseRepository {
    private final DatabaseConnection dbConnection;

    public JdbcCourseRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Course save(Course course) {
        String insertQuery = "INSERT INTO courses (title, description, category, url, instructor_id) " + "VALUES (?, ?, ?, ?, ?) RETURNING *";

        return dbConnection.findOne(insertQuery, this::mapCourse, course.getTitle(), course.getDescription(), course.getCategory(), course.getUrl(), course.getInstructorId());
    }

    @Override
    public Course update(Course entity) {
        String updateQuery = """
                UPDATE courses
                SET title = ?, description = ?, category = ?, url = ?, instructor_id = ?
                WHERE id = ?
                RETURNING *;
                """;

        return dbConnection.findOne(updateQuery, this::mapCourse,
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getUrl(),
                entity.getInstructorId(),
                entity.getId()
        );
    }

    @Override
    public void delete(Integer courseId) {
        String query = "DELETE FROM courses WHERE id = ?";
        dbConnection.execute(query, courseId);
    }

    @Override
    public Optional<Course> findById(Integer courseId) {
        String query = "SELECT * FROM courses WHERE id = ?";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapCourse, courseId));
    }

    @Override
    public List<Course> findAll() {
        String query = "SELECT * FROM courses";
        return dbConnection.findMany(query, this::mapCourse);
    }

    @Override
    public Optional<Course> findByTitle(String title) {
        String query = "SELECT * FROM courses WHERE title = ?;";
        Course course = dbConnection.findOne(query, this::mapCourse, title);
        return Optional.ofNullable(course);
    }

    @Override
    public List<Course> findAllByInstructor(Integer instructorId) {
        String query = "SELECT * FROM courses WHERE instructor_id = ?;";
        return dbConnection.findMany(query, this::mapCourse, instructorId);
    }

    @Override
    public List<Course> findAllByCategory(String category) {
        String query = "SELECT * FROM courses WHERE category = ?;";
        return dbConnection.findMany(query, this::mapCourse, category);
    }

    @Override
    public List<Student> findEnrolledStudents(Integer courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        String query = """
                     SELECT u.id AS user_id, u.username, u.email, u.first_name, u.last_name, u.password_hash,
                            u.last_login, u.is_active, s.progress_percentage, s.completed_courses, s.current_courses
                     FROM enrollments e
                     JOIN users u ON u.id = e.user_id
                     JOIN students s ON s.user_id = u.id
                     WHERE e.course_id = ?
                """;

        return dbConnection.findMany(query, this::mapStudent, courseId);
    }


    @Override
    public void enrollStudent(Integer courseId, Integer studentId) {
        String query = "INSERT INTO enrollments (user_id, course_id) VALUES (?, ?);";

        dbConnection.execute(query, studentId, courseId);
    }

    @Override
    public void unenrollStudent(Integer courseId, Integer studentId) {
        String query = "DELETE FROM enrollments WHERE user_id = ? AND course_id = ?;";

        dbConnection.execute(query, studentId, courseId);
    }


    @Override
    public boolean isStudentEnrolled(Integer studentId, Integer courseId) {
        String query = "SELECT 1 FROM enrollments WHERE user_id = ? AND course_id = ? LIMIT 1";
        return dbConnection.findOne(query, rs -> true, studentId, courseId) != null;
    }


    @Override
    public void ensureCourseExists(Integer courseId) {
        Objects.requireNonNull(courseId, "Course ID cannot be null");
        if (findById(courseId).isEmpty()) {
            throw new IllegalArgumentException("Course with ID '" + courseId + "' does not exist.");
        }
    }

    private Course mapCourse(ResultSet rs) {
        try {
            return new Course(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("category"),
                    rs.getString("url"),
                    rs.getInt("instructor_id"));
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Course", e);
        }
    }

    private Student mapStudent(ResultSet rs) {
        try {
            return new Student(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password_hash"),
                    rs.getTimestamp("last_login") != null ? rs.getTimestamp("last_login").toLocalDateTime() : null,
                    rs.getBoolean("is_active"),
                    rs.getDouble("progress_percentage"),
                    rs.getInt("completed_courses"),
                    rs.getInt("current_courses")
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Student", e);
        }
    }

}
