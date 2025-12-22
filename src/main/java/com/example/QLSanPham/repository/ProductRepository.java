package com.example.QLSanPham.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Kỹ thuật Senior: @EntityGraph
     // Lấy chi tiết sản phẩm + Load luôn Category (Chỉ tốn 1 câu Select)
    @Override
    @EntityGraph(attributePaths = {"category"})
    Optional<Product> findById(Long id);

    // Lấy danh sách sản phẩm phân trang + Load Category
    @Override
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findAll(Pageable pageable);

    // Tìm kiếm sản phẩm theo tên (Có phân trang)
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name, Pageable pageable);

    // Lọc theo Category
    @EntityGraph(attributePaths = {"category"})
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    
    // Tìm sản phẩm active
    Page<Product> findByActiveTrue(Pageable pageable);
}