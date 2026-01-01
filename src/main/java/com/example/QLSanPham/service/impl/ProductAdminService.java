package com.example.QLSanPham.service.impl;

import com.example.QLSanPham.dto.request.ProductRequest;
import com.example.QLSanPham.dto.response.ProductResponse;
import com.example.QLSanPham.entity.Category;
import com.example.QLSanPham.entity.Product;
import com.example.QLSanPham.exception.ProductInUseException;
import com.example.QLSanPham.exception.ResourceNotFoundException;
import com.example.QLSanPham.mapper.ProductMapper;
import com.example.QLSanPham.repository.CategoryRepository;
import com.example.QLSanPham.repository.OrderDetailRepository;
import com.example.QLSanPham.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý business logic cho Product
 * 
 * Best Practices:
 * - @Transactional cho data consistency
 * - Validation logic trước khi save/delete
 * - Exception handling rõ ràng
 * - Logging cho debug và monitoring
 * - DTO pattern để tách biệt layer
 * 
 * @author Senior Developer
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductAdminService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductMapper productMapper;

    /**
     * Lấy danh sách sản phẩm có phân trang
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(int page, int size) {
        log.debug("Fetching products - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAll(pageable);
        
        return productPage.map(productMapper::toResponse);
    }

    /**
     * Tìm kiếm sản phẩm theo tên hoặc danh mục
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, int page, int size) {
        log.debug("Searching products with keyword: '{}', page: {}, size: {}", keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<Product> productPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            productPage = productRepository.findAll(pageable);
        } else {
            productPage = productRepository.findByNameContainingIgnoreCaseOrCategoryNameContainingIgnoreCase(
                keyword.trim(), keyword.trim(), pageable
            );
        }
        
        return productPage.map(productMapper::toResponse);
    }

    /**
     * Lấy sản phẩm theo ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        
        return productMapper.toResponse(product);
    }

    /**
     * Tạo sản phẩm mới
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product: {}", request.getName());
        
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy danh mục với ID: " + request.getCategoryId()
                ));
        
        // Check duplicate product name
        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Sản phẩm với tên '" + request.getName() + "' đã tồn tại!");
        }
        
        // Map DTO to entity
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        
        // Save to database
        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with id: {}", savedProduct.getId());
        
        return productMapper.toResponse(savedProduct);
    }

    /**
     * Cập nhật sản phẩm
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);
        
        // Find existing product
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        
        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Không tìm thấy danh mục với ID: " + request.getCategoryId()
                ));
        
        // Update entity
        productMapper.updateEntity(product, request);
        product.setCategory(category);
        
        // Save changes
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with id: {}", id);
        
        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Xóa sản phẩm (Có kiểm tra ràng buộc)
     * Không cho phép xóa nếu sản phẩm đã có trong đơn hàng
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Attempting to delete product with id: {}", id);
        
        // Check if product exists
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        
        // CRITICAL: Check if product is in any orders
        if (orderDetailRepository.existsByProductId(id)) {
            Long orderCount = orderDetailRepository.countOrdersByProductId(id);
            log.warn("Cannot delete product id {} - exists in {} orders", id, orderCount);
            
            throw new ProductInUseException(
                String.format("Không thể xóa sản phẩm '%s' vì đã có trong %d đơn hàng. " +
                             "Vui lòng đặt trạng thái 'Không hoạt động' thay vì xóa.",
                             product.getName(), orderCount)
            );
        }
        
        // Safe to delete
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }

    /**
     * Toggle trạng thái active/inactive của sản phẩm
     * Alternative cho delete khi sản phẩm đã có đơn hàng
     */
    @Transactional
    public ProductResponse toggleProductStatus(Long id) {
        log.info("Toggling status for product with id: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));
        
        product.setActive(!product.isActive());
        Product updatedProduct = productRepository.save(product);
        
        log.info("Product id {} status changed to: {}", id, updatedProduct.isActive() ? "Active" : "Inactive");
        return productMapper.toResponse(updatedProduct);
    }

    /**
     * Lấy danh sách sản phẩm theo category
     */
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, int page, int size) {
        log.debug("Fetching products for category: {}", categoryId);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);
        
        return productPage.map(productMapper::toResponse);
    }

    /**
     * Lấy tất cả sản phẩm (không phân trang) - dùng cho dropdown
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProductsNoPaging() {
        log.debug("Fetching all products without paging");
        
        List<Product> products = productRepository.findAll(Sort.by("name").ascending());
        return products.stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }
}
