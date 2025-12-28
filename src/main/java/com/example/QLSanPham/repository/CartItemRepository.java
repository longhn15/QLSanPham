package com.example.QLSanPham.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Tìm tất cả cart items của user
    List<CartItem> findByUserId(Long userId);

    // Tìm tất cả cart items theo session ID (cho anonymous users)
    List<CartItem> findBySessionId(String sessionId);

    // Tìm cart item cụ thể của user và product
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

    // Tìm cart item cụ thể theo session và product
    Optional<CartItem> findBySessionIdAndProductId(String sessionId, Long productId);

    // Xóa tất cả items của user
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // Xóa tất cả items theo session
    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);

    // Đếm số lượng items trong giỏ
    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.user.id = :userId")
    Integer countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM CartItem c WHERE c.sessionId = :sessionId")
    Integer countBySessionId(@Param("sessionId") String sessionId);
}   
