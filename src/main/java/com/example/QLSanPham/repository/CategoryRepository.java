package com.example.QLSanPham.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    

    // Tìm category theo tên
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Kiểm tra tồn tại category
    boolean existsByName(String name);

}
