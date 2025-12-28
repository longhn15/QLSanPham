package com.example.QLSanPham.service.impl;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.QLSanPham.entity.Product;
import com.example.QLSanPham.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // Mặc định chỉ đọc -> Tối ưu DB
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        // Repository dùng @EntityGraph nên query cực nhanh
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        if (name == null || name.isEmpty()) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findByNameContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(name, name, pageable);
    }

    public Product getProductById(Long id) {
        Optional<Product> productOPT = productRepository.findById(id);
        return productOPT.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + id));
    }

    public List<Product> getProductByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable).getContent();
    }

}