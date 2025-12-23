package com.example.QLSanPham.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    

    // Tìm category theo tên
    Optional<Category> findByName(String name);
    
    // Kiểm tra tồn tại category
    boolean existsByName(String name);
}
