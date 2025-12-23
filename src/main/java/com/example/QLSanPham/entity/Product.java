package com.example.QLSanPham.entity;

import java.math.BigDecimal;

import com.example.QLSanPham.entity.base.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price; // Giá gốc

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0; // Kho chính (Main Stock)

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    // Quan hệ Lazy để tối ưu truy vấn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // Optimistic Locking: Chống ghi đè khi nhiều admin cùng sửa kho
    @Version
    @Builder.Default
    private Integer version = 0;
}
