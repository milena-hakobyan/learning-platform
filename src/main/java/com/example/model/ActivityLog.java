package com.example.model;

import java.time.LocalDateTime;

public class ActivityLog {
    private int id;
    private int userId;
    private String action;
    private LocalDateTime timestamp;


    public ActivityLog(int id, int userId, String action, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public ActivityLog(int userId, String action) {
        this.userId = userId;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
