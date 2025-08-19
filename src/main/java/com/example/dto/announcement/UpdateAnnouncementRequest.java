package com.example.dto.announcement;

import com.example.annotation.AtLeastOneFieldPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@AtLeastOneFieldPresent(message = "At least one of title or content must be provided")
@Data
@NoArgsConstructor
public class UpdateAnnouncementRequest {
    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String content;
}
