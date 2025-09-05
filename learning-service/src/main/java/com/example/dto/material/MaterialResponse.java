package com.example.dto.material;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MaterialResponse {
    private Long id;
    private String title;
    private String contentType;
    private String category;
    private String url;
    private LocalDateTime uploadDate;
}
