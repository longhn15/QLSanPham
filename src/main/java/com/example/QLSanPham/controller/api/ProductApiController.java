package com.example.QLSanPham.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.QLSanPham.entity.Product;
import com.example.QLSanPham.service.impl.ProductService;

@RestController // Trả về JSON
@RequestMapping("/api")
public class ProductApiController {

    @Autowired
    private ProductService productService;
    
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(required = false) Long categoryId) {
        // Trả về danh sách sản phẩm phân trang, có thể lọc theo categoryId nếu được cung cấp
        PageRequest pageable = PageRequest.of(page, 12); // 12 sản phẩm mỗi trang
        List<Product> products = (categoryId != null)
                ? productService.findByCategoryId(categoryId, pageable)
                : productService.getAllProducts(pageable).getContent();
        return ResponseEntity.ok(products);
    }

    // @GetMapping("/product/{categoryId}")
    // public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId,
    //                                                            @RequestParam(defaultValue = "0") int page) {
    //     // Endpoint theo yêu cầu /api/product/{categoryId} dùng cho UI khi click category
    //     List<Product> products = productService.findByCategoryId(categoryId, PageRequest.of(page, 12)); // 12 sản phẩm mỗi trang
    //     return ResponseEntity.ok(products);
    // }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam(required = false) String q,
                                                        @RequestParam(defaultValue = "0") int page) {
        // Endpoint tìm kiếm sản phẩm theo từ khóa với phân trang
        PageRequest pageable = PageRequest.of(page, 12); // 12 sản phẩm mỗi trang
        List<Product> products = productService.searchProductsByName(q, pageable).getContent();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/api/cart/add/{productId}")
    public ResponseEntity<Product> addToCart(@PathVariable Long productId) {
        // Thêm vào giỏ hàng - chỉ trả về thông tin sản phẩm đã thêm
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(product);
    }
}