package com.example.dto.activityLog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateActivityLogRequest {
    @NotNull
    private Long userId;
    @NotBlank
    private String action;
}