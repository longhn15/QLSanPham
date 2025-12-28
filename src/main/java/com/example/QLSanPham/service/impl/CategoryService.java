package com.example.QLSanPham.service.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.QLSanPham.entity.Category;
import com.example.QLSanPham.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll(Sort.by("name").ascending());
    }

    // --- 2. LẤY TẤT CẢ (Không phân trang - Dùng khi cần thiết) ---
    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("name").ascending());
    }

    // --- 3. Thêm mới hoặc cập nhật ---
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

}
