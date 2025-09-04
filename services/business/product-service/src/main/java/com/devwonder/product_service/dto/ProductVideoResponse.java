package com.devwonder.product_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVideoResponse {
    private Long id;
    private String videoUrl;
    private String thumbnailUrl;
    private String title;
    private String description;
    private Integer displayOrder;
    private Integer duration;
    private LocalDateTime createdAt;
}