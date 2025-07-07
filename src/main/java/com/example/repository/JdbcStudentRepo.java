package com.example.repository;

import com.example.model.Course;
import com.example.model.Student;
import com.example.model.User;
import com.example.utils.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcStudentRepo implements StudentRepository {
    private final DatabaseConnection dbConnection;
    private final UserRepository userRepo;

    public JdbcStudentRepo(DatabaseConnection dbConnection, JdbcUserRepo userRepo) {
        this.dbConnection = dbConnection;
        this.userRepo = userRepo;
    }

    @Override
    public Student save(Student student) {
        User savedUser = userRepo.save(student);

        String insertStudentQuery = "INSERT INTO students (user_id, progress_percentage, completed_courses, current_courses) VALUES (?, ?, ?, ?)";
        dbConnection.execute(insertStudentQuery,
                savedUser.getId(),
                student.getProgressPercentage(),
                student.getCompletedCourses(),
                student.getCurrentCourses()
        );

        return findById(savedUser.getId()).orElseThrow(() -> new RuntimeException("Failed to retrieve saved student"));
    }


    @Override
    public Student update(Student student) {
        String updateQuery = "UPDATE students SET progress_percentage = ?, completed_courses = ?, current_courses = ? WHERE user_id = ?;";

        dbConnection.execute(updateQuery,
                student.getProgressPercentage(),
                student.getCompletedCourses(),
                student.getCurrentCourses(),
                student.getId()
        );

        return findById(student.getId())
                .orElseThrow(() -> new RuntimeException("Failed to retrieve updated student"));
    }


    @Override
    public void delete(Integer userId) {
        String deleteUserQuery = "DELETE FROM users WHERE user_id = ?";
        dbConnection.execute(deleteUserQuery, userId);
    }

    @Override
    public Optional<Student> findById(Integer id) {
        String query = """
                    SELECT u.user_id, u.user_name, u.first_name, u.last_name, u.email, u.password_hash,
                           u.last_login, u.is_active,
                           s.progress_percentage, s.completed_courses, s.current_courses
                    FROM students s
                    JOIN users u ON s.user_id = u.user_id
                    WHERE s.user_id = ?
                """;

        Student student = dbConnection.findOne(query, this::mapToStudent, id);
        return Optional.ofNullable(student);
    }


    @Override
    public List<Student> findAll() {
        String query = """
                    SELECT u.user_id, u.user_name, u.first_name, u.last_name, u.email, u.password_hash,
                           u.last_login, u.is_active,
                           s.progress_percentage, s.completed_courses, s.current_courses
                    FROM students s
                    JOIN users u ON s.user_id = u.user_id
                """;

        return dbConnection.findMany(query, this::mapToStudent);
    }

    @Override
    public List<Course> findAllEnrolledCourses(Integer studentId) {
        String query = """
                    SELECT c.*
                    FROM enrollments e JOIN courses c ON e.course_id = c.id
                    WHERE e.user_id = ?
                """;

        return dbConnection.findMany(query, rs -> {
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
        }, studentId
        );
    }

    @Override
    public void ensureStudentExists(Integer studentId) {
        userRepo.ensureUserExists(studentId);
        if (findById(studentId).isEmpty()) {
            throw new IllegalArgumentException("Student with ID '" + studentId + "' does not exist.");
        }
    }

    private Student mapToStudent(ResultSet rs) {
        try {
            return new Student(
                    rs.getInt("user_id"),
                    rs.getString("user_name"),
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
            throw new RuntimeException("Failed to map ResultSet to Student", e);
        }
    }

}
