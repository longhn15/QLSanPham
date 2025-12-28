package com.example.QLSanPham.service;

import com.example.QLSanPham.dto.request.AddToCartRequest;
import com.example.QLSanPham.dto.request.UpdateCartItemRequest;
import com.example.QLSanPham.dto.response.CartItemResponse;
import com.example.QLSanPham.dto.response.CartResponse;

public interface CartService {
    
    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    CartItemResponse addToCart(AddToCartRequest request, Long userId, String sessionId);
    
    /**
     * Lấy tất cả items trong giỏ hàng
     */
    CartResponse getCart(Long userId, String sessionId);
    
    /**
     * Cập nhật số lượng của cart item
     */
    CartItemResponse updateCartItem(UpdateCartItemRequest request, Long userId, String sessionId);
    
    /**
     * Xóa item khỏi giỏ hàng
     */
    void removeCartItem(Long cartItemId, Long userId, String sessionId);
    
    /**
     * Xóa toàn bộ giỏ hàng
     */
    void clearCart(Long userId, String sessionId);
    
    /**
     * Đếm số lượng items trong giỏ
     */
    Integer getCartItemCount(Long userId, String sessionId);
    
    /**
     * Chuyển giỏ hàng từ session sang user khi đăng nhập
     */
    void mergeSessionCartToUser(String sessionId, Long userId);
}
