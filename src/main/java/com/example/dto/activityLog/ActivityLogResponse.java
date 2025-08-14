package com.example.dto.activityLog;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private LocalDateTime timestamp;
}
