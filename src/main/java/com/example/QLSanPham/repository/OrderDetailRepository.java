package com.example.QLSanPham.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.OrderDetail;

/**
 * Repository cho OrderDetail
 * Dùng để kiểm tra ràng buộc trước khi xóa sản phẩm
 * 
 * @author Senior Developer
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    /**
     * Kiểm tra sản phẩm có tồn tại trong đơn hàng nào không
     * Dùng để validate trước khi xóa sản phẩm
     */
    @Query("SELECT COUNT(od) > 0 FROM OrderDetail od WHERE od.product.id = :productId")
    boolean existsByProductId(@Param("productId") Long productId);

    /**
     * Đếm số lượng đơn hàng có chứa sản phẩm
     */
    @Query("SELECT COUNT(DISTINCT od.order.id) FROM OrderDetail od WHERE od.product.id = :productId")
    Long countOrdersByProductId(@Param("productId") Long productId);
}
