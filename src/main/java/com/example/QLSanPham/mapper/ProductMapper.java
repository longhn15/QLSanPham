package com.example.QLSanPham.mapper;

import org.springframework.stereotype.Component;

import com.example.QLSanPham.dto.request.ProductRequest;
import com.example.QLSanPham.dto.response.ProductResponse;
import com.example.QLSanPham.entity.Product;

/**
 * Mapper cho Product entity và DTO
 * 
 * Best Practice:
 * - Manual mapping cho performance (thay vì MapStruct trong project nhỏ)
 * - Null-safe operations
 * - Clear separation of concerns
 * 
 * @author Senior Developer
 */
@Component
public class ProductMapper {

    /**
     * Convert ProductRequest to Product entity
     */
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
    }

    /**
     * Update existing Product entity from ProductRequest
     */
    public void updateEntity(Product product, ProductRequest request) {
        if (product == null || request == null) {
            return;
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrl(request.getImageUrl());
        if (request.getIsActive() != null) {
            product.setActive(request.getIsActive());
        }
    }

    /**
     * Convert Product entity to ProductResponse
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrl(product.getImageUrl())
                .isActive(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .version(product.getVersion())
                .build();

        // Safely extract category information
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        return response;
    }
}
