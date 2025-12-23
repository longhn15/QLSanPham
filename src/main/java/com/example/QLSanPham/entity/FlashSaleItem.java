package com.example.QLSanPham.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import com.example.QLSanPham.entity.base.BaseEntity;

@Entity
@Table(name = "FlashSaleItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashSaleItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flash_sale_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id")
    private FlashSaleSession flashSaleSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "flash_sale_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal flashSalePrice; // Giá sale

    @Column(name = "original_stock", nullable = false)
    private Integer originalStock; // Số lượng đem ra sale

    @Column(name = "sold_quantity")
    @Builder.Default
    private Integer soldQuantity = 0; // Số lượng đã bán

    @Builder.Default
    private boolean status = true;

    // CỰC KỲ QUAN TRỌNG: Optimistic Locking cho Flash Sale
    // Giúp ngăn chặn việc 1000 người mua nhưng chỉ có 10 hàng
    @Version
    @Builder.Default
    private Integer version = 0;
}
