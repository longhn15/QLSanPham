package com.example.QLSanPham.controller.view.admin;

import com.example.QLSanPham.dto.request.ProductRequest;
import com.example.QLSanPham.dto.response.ProductResponse;
import com.example.QLSanPham.entity.Category;
import com.example.QLSanPham.exception.ProductInUseException;
import com.example.QLSanPham.exception.ResourceNotFoundException;
import com.example.QLSanPham.service.impl.CategoryService;
import com.example.QLSanPham.service.impl.ProductAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controller cho quản lý sản phẩm - Admin Panel
 * 
 * Best Practices:
 * - RESTful URL design
 * - Flash messages cho user feedback
 * - Exception handling với user-friendly messages
 * - Validation với Bean Validation
 * 
 * @author Senior Developer
 */
@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
public class AdminProductController {

    private final ProductAdminService productService;
    private final CategoryService categoryService;

    /**
     * Hiển thị danh sách sản phẩm với tìm kiếm và phân trang
     */
    @GetMapping
    public String listProducts(
            @RequestParam(value = "q", required = false, defaultValue = "") String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model) {
        
        log.debug("Admin accessing product list - query: '{}', page: {}", query, page);
        
        Page<ProductResponse> productPage = productService.searchProducts(query, page, size);
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("searchQuery", query);
        
        return "admin/ProductAdmin";
    }

    /**
     * Hiển thị form thêm sản phẩm mới
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        log.debug("Showing add product form");
        
        ProductRequest productRequest = new ProductRequest();
        List<Category> categories = categoryService.getAllCategories();
        
        model.addAttribute("product", productRequest);
        model.addAttribute("categories", categories);
        model.addAttribute("isEdit", false);
        
        return "admin/FormProduct";
    }

    /**
     * Hiển thị form chỉnh sửa sản phẩm
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        log.debug("Showing edit product form for id: {}", id);
        
        try {
            ProductResponse product = productService.getProductById(id);
            List<Category> categories = categoryService.getAllCategories();
            
            // Map ProductResponse to ProductRequest for form
            ProductRequest productRequest = ProductRequest.builder()
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .stockQuantity(product.getStockQuantity())
                    .imageUrl(product.getImageUrl())
                    .categoryId(product.getCategoryId())
                    .isActive(product.getIsActive())
                    .build();
            
            model.addAttribute("product", productRequest);
            model.addAttribute("productId", id);
            model.addAttribute("categories", categories);
            model.addAttribute("isEdit", true);
            
            return "admin/FormProduct";
            
        } catch (ResourceNotFoundException e) {
            log.error("Product not found: {}", id);
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    /**
     * Xử lý thêm mới sản phẩm
     */
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") ProductRequest productRequest,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {
        
        log.info("Attempting to create product: {}", productRequest.getName());
        
        if (result.hasErrors()) {
            log.warn("Validation errors when creating product");
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("isEdit", false);
            return "admin/FormProduct";
        }
        
        try {
            ProductResponse createdProduct = productService.createProduct(productRequest);
            log.info("Product created successfully: {}", createdProduct.getName());
            ra.addFlashAttribute("successMessage", 
                "Thêm sản phẩm '" + createdProduct.getName() + "' thành công!");
            return "redirect:/admin/products";
            
        } catch (IllegalArgumentException e) {
            log.error("Error creating product: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("isEdit", false);
            return "admin/FormProduct";
        }
    }

    /**
     * Xử lý cập nhật sản phẩm
     */
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("product") ProductRequest productRequest,
            BindingResult result,
            Model model,
            RedirectAttributes ra) {
        
        log.info("Attempting to update product id: {}", id);
        
        if (result.hasErrors()) {
            log.warn("Validation errors when updating product");
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            model.addAttribute("productId", id);
            model.addAttribute("isEdit", true);
            return "admin/FormProduct";
        }
        
        try {
            ProductResponse updatedProduct = productService.updateProduct(id, productRequest);
            log.info("Product updated successfully: {}", updatedProduct.getName());
            ra.addFlashAttribute("successMessage", 
                "Cập nhật sản phẩm '" + updatedProduct.getName() + "' thành công!");
            return "redirect:/admin/products";
            
        } catch (ResourceNotFoundException e) {
            log.error("Product not found: {}", id);
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    /**
     * Xóa sản phẩm (Có kiểm tra ràng buộc)
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes ra) {
        log.info("Attempting to delete product id: {}", id);
        
        try {
            productService.deleteProduct(id);
            log.info("Product deleted successfully: {}", id);
            ra.addFlashAttribute("successMessage", "Xóa sản phẩm thành công!");
            
        } catch (ProductInUseException e) {
            log.warn("Cannot delete product due to constraints: {}", e.getMessage());
            ra.addFlashAttribute("errorMessage", e.getMessage());
            
        } catch (ResourceNotFoundException e) {
            log.error("Product not found: {}", id);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/products";
    }

    /**
     * Toggle trạng thái active/inactive
     */
    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Long id, RedirectAttributes ra) {
        log.info("Toggling status for product id: {}", id);
        
        try {
            ProductResponse product = productService.toggleProductStatus(id);
            String status = product.getIsActive() ? "kích hoạt" : "vô hiệu hóa";
            ra.addFlashAttribute("successMessage", 
                "Đã " + status + " sản phẩm '" + product.getName() + "'!");
            
        } catch (ResourceNotFoundException e) {
            log.error("Product not found: {}", id);
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        
        return "redirect:/admin/products";
    }
}
