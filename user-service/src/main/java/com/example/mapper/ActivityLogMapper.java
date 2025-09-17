package com.example.mapper;

import com.example.dto.activityLog.ActivityLogResponse;
import com.example.dto.activityLog.CreateActivityLogRequest;
import com.example.model.ActivityLog;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ActivityLogMapper {

    public ActivityLog toEntity(CreateActivityLogRequest request, Long userId) {
        ActivityLog log = new ActivityLog();
        log.setUserId(userId);
        log.setAction(request.getAction());
        log.setTimestamp(LocalDateTime.now());
        return log;
    }

    public ActivityLogResponse toDto(ActivityLog entity) {
        ActivityLogResponse dto = new ActivityLogResponse();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setAction(entity.getAction());
        dto.setTimestamp(entity.getTimestamp());
        return dto;
    }
}