package com.example.QLSanPham.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;
    
    private String email;
    
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String shippingAddress;
    
    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod; // cod, bank, etc
    
    private String shippingMethod; // standard, express
    
    private String note;
    
    @NotEmpty(message = "Giỏ hàng không được trống")
    private List<OrderItemRequest> cartItems;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        // Backward compatibility: previously `id` was used for both product and cart item
        // Prefer explicit fields below; `id` may be null and should be ignored.
        private Long id; // legacy
        private Long productId; // product identifier
        private Long cartItemId; // cart item identifier
        private Integer quantity;
        private Double price;
    }
}
