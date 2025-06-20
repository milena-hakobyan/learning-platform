package com.example.repository;

import com.example.model.ActivityLog;

import java.util.List;

public interface ActivityLogRepository extends CrudRepository<ActivityLog, Integer> {
    List<ActivityLog> findByUserId(int userId);
}
