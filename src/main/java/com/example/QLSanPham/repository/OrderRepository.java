package com.example.QLSanPham.repository;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.Order;
import com.example.QLSanPham.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Lịch sử mua hàng của User (Sắp xếp mới nhất trước)
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // --- CÁC QUERY CHO ADMIN DASHBOARD (ĐIỂM CAO) ---

    // 1. Tính tổng doanh thu trong ngày
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    BigDecimal sumRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 2. Đếm số đơn hàng thành công hôm nay
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end AND o.status <> 'CANCELLED'")
    Long countOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // 3. Top 5 user mua nhiều tiền nhất (Native Query cho hiệu năng cao)
    @Query(value = "SELECT u.full_name, SUM(o.total_amount) AS total " +
                   "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                   "WHERE o.status = 'COMPLETED' " +
                   "GROUP BY u.full_name " +
                   "ORDER BY total DESC LIMIT 5", nativeQuery = true)
    List<Object[]> findTopSpenders();

        @Query("""
        SELECT DISTINCT o FROM Order o
        LEFT JOIN o.orderDetails d
        LEFT JOIN d.flashSaleItem f
        WHERE (:status IS NULL OR o.status = :status)
            AND (:isFlashSale IS NULL OR (:isFlashSale = TRUE AND f IS NOT NULL) OR (:isFlashSale = FALSE AND f IS NULL))
            AND (
                :keyword IS NULL OR :keyword = '' OR
                LOWER(o.shippingAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(o.paymentMethod) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(o.note) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(CONCAT(o.id, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                (o.user IS NOT NULL AND LOWER(o.user.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            )
        """)
        Page<Order> searchOrders(@Param("status") OrderStatus status,
                @Param("isFlashSale") Boolean isFlashSale,
                @Param("keyword") String keyword,
                Pageable pageable);

        long countByStatus(OrderStatus status);
}