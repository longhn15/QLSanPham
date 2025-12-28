package com.example.QLSanPham.controller.view.user;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.QLSanPham.entity.Product;
import com.example.QLSanPham.service.impl.CategoryService;
import com.example.QLSanPham.service.impl.ProductService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserHomePageController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/home")
    public String home(Model model, @RequestParam(required = false) String q) {
        model.addAttribute("categorys", categoryService.getAllCategories());
        
        // Lấy danh sách sản phẩm thường - chuyển Page sang List
        Page<Product> products = productService.searchProductsByName(q, PageRequest.of(0, 12));
        model.addAttribute("products", products.getContent());
        model.addAttribute("sizeProducts", products.getTotalElements());
        
        // Thêm thông tin tìm kiếm
        model.addAttribute("searchKeyword", q);
        model.addAttribute("isSearchMode", q != null && !q.trim().isEmpty());
        
        return "user/HomePage"; // File HTML giao diện trang chủ
    }

    @GetMapping("/category/{id}")
    public String categoryProducts(@PathVariable Long id, Model model) {
        model.addAttribute("products", productService.findByCategoryId(id, PageRequest.of(0, 12)));
        return "user/HomePage"; // user/HomePage.html
    }

    @GetMapping("/product/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        // Lấy chi tiết sản phẩm
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        
        // Lấy sản phẩm liên quan (cùng danh mục, loại trừ sản phẩm hiện tại, lấy 4-5 sản phẩm)
        List<Product> relatedProducts = productService.findByCategoryId(product.getCategory().getId(), PageRequest.of(0, 5))
            .stream()
            .filter(p -> !p.getId().equals(id))
            .limit(4)
            .toList();
        model.addAttribute("related", relatedProducts);
        
        return "user/ProductDetails"; // Trả về tên file HTML hiển thị chi tiết sản phẩm
    }
}
