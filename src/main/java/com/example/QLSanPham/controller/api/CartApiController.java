package com.example.QLSanPham.controller.api;

import com.example.QLSanPham.dto.request.AddToCartRequest;
import com.example.QLSanPham.dto.request.UpdateCartItemRequest;
import com.example.QLSanPham.dto.response.CartItemResponse;
import com.example.QLSanPham.dto.response.CartResponse;
import com.example.QLSanPham.security.UserDetailsServiceImpl;
import com.example.QLSanPham.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartApiController {

    private final CartService cartService;

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
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã thêm sản phẩm vào giỏ hàng");
            result.put("data", response);
            result.put("cartItemCount", cartService.getCartItemCount(userId, sessionId));
            
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
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã cập nhật giỏ hàng");
            result.put("data", response);
            
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
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đã xóa sản phẩm khỏi giỏ hàng");
            result.put("cartItemCount", cartService.getCartItemCount(userId, sessionId));
            
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
        if (userDetails instanceof UserDetailsServiceImpl.CustomUserDetails) {
            return ((UserDetailsServiceImpl.CustomUserDetails) userDetails).getId();
        }
        return null;
    }
}
