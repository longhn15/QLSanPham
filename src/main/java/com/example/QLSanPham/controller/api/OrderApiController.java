package com.example.QLSanPham.controller.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.QLSanPham.dto.request.CreateOrderRequest;
import com.example.QLSanPham.security.UserDetailsServiceImpl;
import com.example.QLSanPham.service.impl.OrderService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/order")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    /**
     * Create order from selected cart items
     */
    @PostMapping("/create")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        try {
            Long userId = getUserId(userDetails);
            String sessionId = session.getId();

            var response = orderService.createOrder(request, userId, sessionId);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "Đơn hàng được tạo thành công! Vui lòng chờ xác nhận");
            result.put("data", response);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Có lỗi xảy ra khi tạo đơn hàng: " + e.getMessage()
            ));
        }
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsServiceImpl.CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        return null;
    }
}
