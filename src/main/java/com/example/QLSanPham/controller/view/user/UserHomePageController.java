package com.example.QLSanPham.controller.view.user;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import com.example.QLSanPham.service.impl.CategoryService;
import com.example.QLSanPham.service.impl.ProductService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserHomePageController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("categorys", categoryService.getAllCategories());
        
        // Lấy danh sách sản phẩm thường
        model.addAttribute("products", productService.getAllProducts(Pageable.ofSize(10)));
        
        // return "/user/HomePage"; // File HTML giao diện trang chủ
        return "/user/FakeHomePage"; // File HTML giao diện trang chủ
    }

    // @GetMapping("/product/{id}")
    // public String productDetail(@PathVariable Long id, Model model) {
    //     model.addAttribute("product", productService.getProductById(id));
    //     return "user/product-detail";
    // }
}
