package com.example.mapper;

import com.example.dto.activityLog.ActivityLogResponse;
import com.example.dto.activityLog.CreateActivityLogRequest;
import com.example.model.ActivityLog;
import com.example.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActivityLogMapper {

    public ActivityLog toEntity(CreateActivityLogRequest request, User user) {
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(request.getAction());
        log.setTimestamp(LocalDateTime.now());
        return log;
    }

    public ActivityLogResponse toDto(ActivityLog entity) {
        ActivityLogResponse dto = new ActivityLogResponse();
        dto.setUserId(entity.getUser().getId());
        dto.setUsername(entity.getUser().getUsername());
        dto.setAction(entity.getAction());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}
