package com.example.dto.activityLog;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateActivityLogRequest {
    private Long userId;
    private String action;
}