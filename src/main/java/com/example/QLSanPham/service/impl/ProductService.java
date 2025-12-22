// package com.example.QLSanPham.service.impl;


// import com.example.QLSanPham.dto.ProductDTO;
// import com.example.QLSanPham.entity.Product;
// import com.example.QLSanPham.exception.ResourceNotFoundException;
// import com.example.QLSanPham.repository.ProductRepository;
// import com.example.QLSanPham.mapper.ProductMapper; // Giả sử bạn dùng MapStruct
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @RequiredArgsConstructor
// @Transactional(readOnly = true) // Mặc định chỉ đọc -> Tối ưu DB
// public class ProductService {

//     private final ProductRepository productRepository;
//     private final ProductMapper productMapper;

//     public Page<ProductDTO> getAllProducts(Pageable pageable) {
//         // Repository dùng @EntityGraph nên query cực nhanh
//         return productRepository.findAll(pageable)
//                 .map(productMapper::toDTO);
//     }

//     public ProductDTO getProductById(Long id) {
//         Product product = productRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm id: " + id));
//         return productMapper.toDTO(product);
//     }

//     // Chỉ method ghi mới mở Transaction write
//     @Transactional
//     public ProductDTO createProduct(ProductDTO dto) {
//         Product entity = productMapper.toEntity(dto);
//         // Logic nghiệp vụ khác nếu có...
//         return productMapper.toDTO(productRepository.save(entity));
//     }
// }