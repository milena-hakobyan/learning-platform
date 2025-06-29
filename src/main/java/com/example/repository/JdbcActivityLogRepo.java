package com.example.repository;

import com.example.model.ActivityLog;
import com.example.utils.DatabaseConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class JdbcActivityLogRepo implements ActivityLogRepository {
    private final DatabaseConnection dbConnection;

    public JdbcActivityLogRepo(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    @Override
    public ActivityLog save(ActivityLog entity) {
        String insertQuery = "INSERT INTO activity_logs (user_id, action) VALUES (?, ?) RETURNING *";

        return dbConnection.findOne(insertQuery, this::mapActivityLog, entity.getUserId(), entity.getAction());
    }

    @Override
    public ActivityLog update(ActivityLog entity) {
        String updateQuery = "UPDATE activity_logs SET user_id = ?, action = ?, timestamp = ? WHERE id = ?";
        return dbConnection.findOne(updateQuery, this::mapActivityLog,entity.getUserId(), entity.getAction(), entity.getTimestamp(), entity.getId());
    }

    @Override
    public void delete(Integer logId) {
        String deleteQuery = "DELETE FROM activity_logs WHERE id = ?";
        dbConnection.execute(deleteQuery, logId);
    }

    @Override
    public Optional<ActivityLog> findById(Integer logId) {
        String query = "SELECT * FROM activity_logs WHERE id = ?";
        return Optional.ofNullable(dbConnection.findOne(query, this::mapActivityLog, logId));
    }

    @Override
    public List<ActivityLog> findAll() {
        String query = "SELECT * FROM activity_logs ORDER BY timestamp DESC";
        return dbConnection.findMany(query, this::mapActivityLog);
    }

    @Override
    public List<ActivityLog> findAllByUserId(int userId) {
        String query = "SELECT * FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC";
        return dbConnection.findMany(query, this::mapActivityLog, userId);
    }

    private ActivityLog mapActivityLog(ResultSet rs) {
        try {
            return new ActivityLog(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getString("action"),
                    rs.getTimestamp("timestamp").toLocalDateTime()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping ActivityLog", e);
        }
    }
}
