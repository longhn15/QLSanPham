package com.example.QLSanPham.controller.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.QLSanPham.dto.request.AddToCartRequest;
import com.example.QLSanPham.dto.request.UpdateCartItemRequest;
import com.example.QLSanPham.dto.response.CartItemResponse;
import com.example.QLSanPham.dto.response.CartResponse;
import com.example.QLSanPham.entity.CartItem;
import com.example.QLSanPham.repository.CartItemRepository;
import com.example.QLSanPham.security.UserDetailsServiceImpl;
import com.example.QLSanPham.service.CartService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;

    /**
     * Lấy thông tin chi tiết các sản phẩm được chọn từ localStorage
     * Request: { selectedItemIds: [1, 2, 3] }
     */
    @PostMapping("/get-selected-items")
    public ResponseEntity<?> getSelectedItems(
            @RequestBody Map<String, List<Long>> request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            List<Long> selectedIds = request.get("selectedItemIds");
            
            if (selectedIds == null || selectedIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không có sản phẩm được chọn"
                ));
            }

            // Lấy các cart items từ database
            List<CartItem> cartItems = cartItemRepository.findAllById(selectedIds);
            
            // Map sang CartItemResponse với đầy đủ thông tin sản phẩm
            List<CartItemResponse> items = cartItems.stream()
                .map(this::mapCartItemToResponse)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", items
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Thêm sản phẩm vào giỏ hàng
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            CartItemResponse response = cartService.addToCart(request, userId, sessionId);
            Integer count = cartService.getCartItemCount(userId, sessionId);
            session.setAttribute("CART_COUNT", count);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã thêm sản phẩm vào giỏ hàng");
            result.put("data", response);
            result.put("cartItemCount", count);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Lấy giỏ hàng
     */
    @GetMapping
    public ResponseEntity<?> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            CartResponse response = cartService.getCart(userId, sessionId);
            session.setAttribute("CART_COUNT", response.getTotalItems());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            CartItemResponse response = cartService.updateCartItem(request, userId, sessionId);
            Integer count = cartService.getCartItemCount(userId, sessionId);
            session.setAttribute("CART_COUNT", count);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã cập nhật giỏ hàng");
            result.put("data", response);
            result.put("cartItemCount", count);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Long cartItemId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            cartService.removeCartItem(cartItemId, userId, sessionId);
            Integer count = cartService.getCartItemCount(userId, sessionId);
            session.setAttribute("CART_COUNT", count);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã xóa sản phẩm khỏi giỏ hàng");
            result.put("cartItemCount", count);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Xóa toàn bộ giỏ hàng
     */
    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            cartService.clearCart(userId, sessionId);
            session.setAttribute("CART_COUNT", 0);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đã xóa toàn bộ giỏ hàng"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Lấy số lượng items trong giỏ
     */
    @GetMapping("/count")
    public ResponseEntity<?> getCartItemCount(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            Integer count = cartService.getCartItemCount(userId, sessionId);
            session.setAttribute("CART_COUNT", count);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails == null) {
            System.out.println("UserDetails is null - anonymous user");
            return null;
        }
        
        System.out.println("UserDetails type: " + userDetails.getClass().getName());
        System.out.println("UserDetails username: " + userDetails.getUsername());
        
        if(userDetails instanceof UserDetailsServiceImpl.CustomUserDetails customUserDetails) {
            Long userId = customUserDetails.getId();
            System.out.println("Extracted user ID: " + userId);
            return userId;
        }
        
        System.out.println("UserDetails is not CustomUserDetails - returning null");
        return null;
    }

    /**
     * Helper method: Map CartItem entity to CartItemResponse DTO
     */
    private CartItemResponse mapCartItemToResponse(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productImage(cartItem.getProduct().getImageUrl())
                .categoryName(cartItem.getProduct().getCategory().getName())
                .price(cartItem.getPriceSnapshot())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .stockQuantity(cartItem.getProduct().getStockQuantity())
                .build();
    }
}
