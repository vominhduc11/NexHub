package com.devwonder.product_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_videos")
@Data
public class ProductVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    private Integer duration;

    @Column(length = 50)
    private String type;
}