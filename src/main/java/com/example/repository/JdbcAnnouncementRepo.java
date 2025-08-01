package com.example.repository;

import com.example.model.Announcement;
import com.example.utils.DatabaseConnection;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAnnouncementRepo implements AnnouncementRepository {
    private final DatabaseConnection dbConnection;

    public JdbcAnnouncementRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public Announcement save(Announcement entity) {
        if (entity == null) throw new IllegalArgumentException("Announcement cannot be null");

        String query = "INSERT INTO announcements (title, content, instructor_id, course_id) " +
                "VALUES (?, ?, ?, ?) RETURNING *";

        return dbConnection.findOne(query, this::mapAnnouncement, entity.getTitle(), entity.getContent(), entity.getPostedById(), entity.getCourseId());
    }


    @Override
    public Announcement update(Announcement announcement) {
        if (announcement == null || announcement.getId() == null) {
            throw new IllegalArgumentException("Announcement or Announcement ID cannot be null");
        }

        String updateQuery = """
                    UPDATE announcements SET
                        title = ?,
                        content = ?,
                        instructor_id = ?,
                        course_id = ?
                    WHERE id = ?
                    RETURNING *;
                """;

        return dbConnection.findOne(updateQuery, this::mapAnnouncement,
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getPostedById(),
                announcement.getCourseId(),
                announcement.getId()
        );
    }


    @Override
    public void delete(Integer announcementId) {
        if (announcementId == null) throw new IllegalArgumentException("Annoucement Id cannot be null");

        String query = "DELETE FROM announcements WHERE id = ?";
        dbConnection.execute(query, announcementId);
    }

    @Override
    public Optional<Announcement> findById(Integer announcementId) {
        if (announcementId == null) throw new IllegalArgumentException("Announcement Id cannot be null");

        String query = "SELECT * FROM announcements WHERE id = ?;";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapAnnouncement, announcementId));
    }

    @Override
    public List<Announcement> findAll() {
        String query = "SELECT * FROM announcements;";
        return dbConnection.findMany(query, this::mapAnnouncement);
    }

    @Override
    public List<Announcement> findAllByCourseId(Integer courseId) {
        if (courseId == null) throw new IllegalArgumentException("Course Id cannot be null");

        String query = "SELECT * FROM announcements WHERE course_id = ?;";
        return dbConnection.findMany(query, this::mapAnnouncement, courseId);
    }

    @Override
    public List<Announcement> findAllByInstructorId(Integer instructorId) {
        if (instructorId == null) throw new IllegalArgumentException("Instructor Id cannot be null");

        String query = "SELECT * FROM announcements WHERE instructor_id = ?;";
        return dbConnection.findMany(query, this::mapAnnouncement, instructorId);
    }

    private Announcement mapAnnouncement(ResultSet rs) {
        try {
            return new Announcement(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getInt("instructor_id"),
                    rs.getInt("course_id"),
                    rs.getTimestamp("posted_date").toLocalDateTime()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ResultSet to Announcement", e);
        }
    }

}
