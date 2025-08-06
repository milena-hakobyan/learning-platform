package com.example.dto.announcement;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CreateAnnouncementRequest {
    private String title;
    private String content;
    private Long instructorId;
    private Long courseId;
}