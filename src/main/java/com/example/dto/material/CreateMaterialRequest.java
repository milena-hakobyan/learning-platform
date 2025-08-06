package com.example.dto.material;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateMaterialRequest {
    private String title;
    private String contentType;
    private String category;
    private String url;
    private Long instructorId;
}
