// package com.example.QLSanPham.repository;

// import java.util.Optional;

// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.EntityGraph;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import com.example.QLSanPham.entity.FlashSaleItem;

// @Repository
// public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

//     // Lấy tất cả sản phẩm trong 1 phiên Sale cụ thể
//     // @EntityGraph giúp lấy thông tin Product đi kèm luôn
//     @EntityGraph(attributePaths = {"product", "product.category"})
//     Page<FlashSaleItem> findByFlashSaleSessionIdAndStatusTrue(Long sessionId, Pageable pageable);

//     // Tìm item sale cụ thể để check kho khi user đặt hàng
//     // Cần lấy Product để validate
//     @EntityGraph(attributePaths = {"product"})
//     Optional<FlashSaleItem> findByIdAndStatusTrue(Long id);
    
//     // Kiểm tra xem 1 sản phẩm đã có trong phiên sale chưa (Tránh add trùng)
//     boolean existsByFlashSaleSessionIdAndProductId(Long sessionId, Long productId);
// }
