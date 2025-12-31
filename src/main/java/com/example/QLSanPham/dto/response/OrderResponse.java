package com.example.QLSanPham.dto.response;

import com.example.QLSanPham.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private String note;
    
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .orderNumber("ORD-" + order.getId() + "-" + System.currentTimeMillis())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().toString())
                .paymentMethod(order.getPaymentMethod())
                .shippingAddress(order.getShippingAddress())
                .orderDate(order.getOrderDate())
                .note(order.getNote())
                .build();
    }
}
