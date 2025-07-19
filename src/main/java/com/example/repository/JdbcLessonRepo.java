package com.example.repository;

import com.example.model.Lesson;
import com.example.model.Material;
import com.example.utils.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Repository
public class JdbcLessonRepo implements LessonRepository {
    private final DatabaseConnection dbConnection;

    public JdbcLessonRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Lesson save(Lesson lesson) {
        String query = "INSERT INTO lessons (title, content_description, course_id) VALUES (?, ?, ?) RETURNING *";

        return dbConnection.findOne(query, this::mapLesson, lesson.getTitle(), lesson.getContent(), lesson.getCourseId());
    }

    @Override
    public Lesson update(Lesson lesson) {
        String query = "UPDATE lessons SET title = ?, content_description = ?, course_id = ? WHERE id = ? RETURNING *";
        return dbConnection.findOne(query, this::mapLesson,
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getCourseId(),
                lesson.getId()
        );
    }


    @Override
    public void delete(Integer lessonId) {
        String query = "DELETE FROM lessons WHERE id = ?";
        dbConnection.execute(query, lessonId);
    }

    @Override
    public Optional<Lesson> findById(Integer lessonId) {
        String query = "SELECT * FROM lessons WHERE id = ?";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapLesson, lessonId));
    }

    @Override
    public List<Lesson> findAll() {
        String query = "SELECT * FROM lessons";

        return dbConnection.findMany(query, this::mapLesson);
    }

    @Override
    public List<Lesson> findAllByCourseId(Integer courseId) {
        String query = "SELECT * FROM lessons WHERE course_id = ?";
        return dbConnection.findMany(query, this::mapLesson, courseId);
    }

    @Override
    public List<Lesson> findAllLessonsByInstructorId(Integer instructorId) {
        String query = """
                    SELECT l.* FROM lessons l
                    JOIN courses c ON l.course_id = c.id
                    WHERE c.instructor_id = ?
                """;

        return dbConnection.findMany(query, this::mapLesson, instructorId);
    }

    @Override
    public List<Material> findAllMaterialsByLessonId(Integer lessonId) {
        String query = """
                    SELECT m.*
                    FROM materials m
                    JOIN lesson_materials lm ON m.id = lm.material_id
                    WHERE lm.lesson_id = ?
                """;

        return dbConnection.findMany(query, rs -> {
            try {
                return new Material(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content_type"),
                        rs.getString("category"),
                        rs.getString("url"),
                        rs.getInt("instructor_id"),
                        rs.getTimestamp("upload_date").toLocalDateTime()
                );
            } catch (SQLException e) {
                throw new RuntimeException("Error mapping ResultSet to Material", e);
            }
        }, lessonId);
    }


    @Override
    public void addMaterial(Integer lessonId, Material material) {
        String query = "INSERT INTO materials (title, content_type, category, url, instructor_id) VALUES (?, ?, ?, ?, ?);";

        dbConnection.execute(query,
                material.getTitle(),
                material.getContentType(),
                material.getCategory(),
                material.getUrl(),
                material.getInstructorId()
        );

        Integer materialId = dbConnection.findOne("SELECT id FROM materials WHERE title = ? AND content_type = ? AND url = ? AND instructor_id = ? ORDER BY id DESC LIMIT 1;", rs -> {
            try {
                return rs.getInt("id");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get generated material ID", e);
            }
        }, material.getTitle(),
                material.getContentType(),
                material.getUrl(),
                material.getInstructorId());

        String query2 = "INSERT INTO lesson_materials (lesson_id, material_id) VALUES (?, ?);";
        dbConnection.execute(query2, lessonId, materialId);

    }

    @Override
    public void removeMaterial(Integer lessonId, Integer materialId) {
        String query = "DELETE FROM lesson_materials WHERE lesson_id = ? AND material_id = ?;";

        dbConnection.execute(query, lessonId, materialId);
    }

    @Override
    public void ensureStudentAccessToLesson(Integer studentId, Integer lessonId) {
        String query = """
        SELECT 1
        FROM lessons l
            JOIN enrollments e ON l.course_id = e.course_id
        WHERE e.student_id = ? AND l.id = ? LIMIT 1;
    """;

        boolean hasAccess = dbConnection.findOne(query, rs -> true, studentId, lessonId) != null;

        if (!hasAccess) {
            throw new IllegalArgumentException("Student " + studentId + " has no access to lesson " + lessonId);
        }
    }



    @Override
    public void ensureLessonExists(Integer lessonId) {
        Objects.requireNonNull(lessonId, "Lesson ID cannot be null");
        if (findById(lessonId).isEmpty()) {
            throw new IllegalArgumentException("Lesson with ID '" + lessonId + "' does not exist.");
        }
    }

    private Lesson mapLesson(ResultSet rs) {
        try {
            return new Lesson(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content_description"),
                    rs.getInt("course_id"),
                    rs.getTimestamp("upload_date").toLocalDateTime()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Lesson", e);
        }
    }


}
