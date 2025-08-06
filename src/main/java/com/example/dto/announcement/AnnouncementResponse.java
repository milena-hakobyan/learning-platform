package com.example.dto.announcement;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private String instructorName;
    private String courseTitle;
    private LocalDateTime postedAt;
}