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
    @Query(value = "SELECT TOP 5 u.full_name, SUM(o.total_amount) as total " +
                   "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                   "WHERE o.status = 'COMPLETED' " +
                   "GROUP BY u.full_name " +
                   "ORDER BY total DESC", nativeQuery = true)
    List<Object[]> findTopSpenders();
}