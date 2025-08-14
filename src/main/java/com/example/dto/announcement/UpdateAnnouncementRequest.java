package com.example.dto.announcement;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateAnnouncementRequest {
    private String title;
    private String content;
}