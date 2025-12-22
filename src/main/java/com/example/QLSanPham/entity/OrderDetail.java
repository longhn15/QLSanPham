package com.example.QLSanPham.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

import com.example.QLSanPham.entity.base.BaseEntity;

@Entity
@Table(name = "OrderDetails")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // Có thể null nếu mua thường (không qua FlashSale)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_item_id")
    private FlashSaleItem flashSaleItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_purchase", nullable = false, precision = 18, scale = 2)
    private BigDecimal priceAtPurchase; // Giá tại thời điểm mua
}

enum OrderStatus {
    PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}