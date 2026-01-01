package com.example.QLSanPham.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO cho response sản phẩm
 * 
 * Best Practice:
 * - Chỉ trả về thông tin cần thiết
 * - Flatten nested objects để tránh lazy loading issues
 * - Immutable response với @Builder
 * 
 * @author Senior Developer
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private Boolean isActive;
    
    // Flattened Category information
    private Long categoryId;
    private String categoryName;
    
    // Audit fields
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Version for optimistic locking
    private Integer version;
    
    /**
     * Helper method to display stock status
     */
    public String getStockStatus() {
        if (stockQuantity == null || stockQuantity == 0) {
            return "Hết hàng";
        } else if (stockQuantity < 10) {
            return "Sắp hết";
        } else {
            return "Còn hàng";
        }
    }
}
