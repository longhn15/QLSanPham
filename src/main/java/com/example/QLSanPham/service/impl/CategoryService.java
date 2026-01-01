package com.example.QLSanPham.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.QLSanPham.entity.Category;
import com.example.QLSanPham.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    /**
     * Lấy tất cả danh mục (không phân trang)
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll(Sort.by("name").ascending());
    }

    /**
     * Tìm kiếm Category theo tên với phân trang
     * @param q từ khóa tìm kiếm (có thể null hoặc rỗng)
     * @param page số trang bắt đầu từ 0
     * @param size số lượng bản ghi trên mỗi trang
     */
    public Page<Category> search(String q, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if (q == null || q.trim().isEmpty()) {
            return categoryRepository.findAll(pageable);
        } else {
            return categoryRepository.findByNameContainingIgnoreCase(q.trim(), pageable);
        }
    }

    /**
     * Lấy tất cả danh mục (không phân trang)
     */
    public List<Category> findAll() {
        return categoryRepository.findAll(Sort.by("name").ascending());
    }

    /**
     * Thêm mới hoặc cập nhật danh mục
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Lấy chi tiết danh mục theo ID (dùng cho edit, view)
     */
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Kiểm tra danh mục có tồn tại hay không
     */
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    /**
     * Kiểm tra tên danh mục có tồn tại hay không
     */
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    /**
     * Xóa danh mục theo ID
     */
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

}
