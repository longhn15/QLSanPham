package com.example.QLSanPham.entity;

import com.example.QLSanPham.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "CartItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;

    // Session ID cho anonymous users
    @Column(name = "session_id", length = 255)
    private String sessionId;

    // Giá tại thời điểm thêm vào giỏ (để tránh thay đổi giá ảnh hưởng)
    @Column(name = "price_snapshot", precision = 18, scale = 2)
    private BigDecimal priceSnapshot;

    @PrePersist
    public void prePersist() {
        if (this.priceSnapshot == null && this.product != null) {
            this.priceSnapshot = this.product.getPrice();
        }
    }

    // Helper method để tính tổng tiền của item này
    public BigDecimal getTotalPrice() {
        return priceSnapshot.multiply(BigDecimal.valueOf(quantity));
    }
}
