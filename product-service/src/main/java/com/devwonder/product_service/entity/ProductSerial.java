package com.devwonder.product_service.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "product_serials")
@Data
public class ProductSerial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String serial;

    @ManyToOne
    @JoinColumn(name = "id_product", nullable = false)
    private Product product;
}