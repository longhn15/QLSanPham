package com.example.QLSanPham.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.QLSanPham.entity.base.BaseEntity;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus status = OrderStatus.PENDING; // PENDING, PAID, SHIPPING, COMPLETED, CANCELLED

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "shipping_address", length = 255)
    private String shippingAddress;

    @Column(length = 500)
    private String note;

    @Column(name = "order_date")
    private LocalDateTime orderDate;


    // Cascade ALL để khi lưu Order thì tự lưu luôn OrderDetails
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // Helper method để thêm detail dễ dàng (Clean Code)
    public void addDetail(OrderDetail detail) {
        orderDetails.add(detail);
        detail.setOrder(this);
    }
}

// ---------------------------------------------------------
