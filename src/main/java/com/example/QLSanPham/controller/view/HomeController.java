// package com.example.QLSanPham.controller.view;


// import com.example.QLSanPham.service.FlashSaleService;
// import com.example.QLSanPham.service.ProductService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;

// @Controller
// @RequiredArgsConstructor
// public class HomeController {

//     private final ProductService productService;
//     private final FlashSaleService flashSaleService;

//     @GetMapping("/")
//     public String home(Model model) {
//         // Lấy Flash Sale đang active để show đồng hồ đếm ngược và list hàng hot
//         model.addAttribute("activeFlashSale", flashSaleService.getCurrentActiveSession());
        
//         // Lấy danh sách sản phẩm thường
//         model.addAttribute("products", productService.getAllProducts(Pageable.ofSize(10)));
        
//         return "user/home"; // File HTML giao diện trang chủ
//     }

//     @GetMapping("/product/{id}")
//     public String productDetail(@PathVariable Long id, Model model) {
//         model.addAttribute("product", productService.getProductById(id));
//         return "user/product-detail";
//     }
// }
