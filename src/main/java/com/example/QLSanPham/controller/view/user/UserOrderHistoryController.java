package com.example.QLSanPham.controller.view.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.QLSanPham.dto.response.CartResponse;
import com.example.QLSanPham.entity.Order;
import com.example.QLSanPham.security.UserDetailsServiceImpl;
import com.example.QLSanPham.service.CartService;
import com.example.QLSanPham.service.impl.OrderService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserOrderHistoryController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @GetMapping("/order-history")
    public String viewOrderHistory(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long orderId,
            HttpSession session) {

        Long userId = getUserId(userDetails);
        String sessionId = session.getId();

        CartResponse response = cartService.getCart(userId, sessionId);
        session.setAttribute("CART_COUNT", response.getTotalItems());
        
        
        if (userId == null) {
            return "redirect:/auth/login";
        }

        // Get order history
        var orders = orderService.getUserOrders(userId, PageRequest.of(page, 10));
        model.addAttribute("orders", orders);
        
        // If specific order requested, highlight it
        if (orderId != null) {
            model.addAttribute("newOrderId", orderId);
        }
        
        return "user/OrderHistory";
    }

    @GetMapping("/order-detail/{id}")
    public String viewOrderDetail(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        Long userId = getUserId(userDetails);
        
        if (userId == null) {
            return "redirect:/auth/login";
        }

        // Get cart count for header
        String sessionId = session.getId();
        CartResponse response = cartService.getCart(userId, sessionId);
        session.setAttribute("CART_COUNT", response.getTotalItems());

        // Get order detail
        Order order = orderService.getOrderById(id);
        
        // Security check: ensure order belongs to current user
        if (order == null || !order.getUser().getId().equals(userId)) {
            return "redirect:/user/order-history";
        }

        model.addAttribute("order", order);
        return "user/OrderDetail";
    }

    @PostMapping("/cancel-order/{id}")
    public String cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        Long userId = getUserId(userDetails);
        
        if (userId == null) {
            return "redirect:/auth/login";
        }

        try {
            orderService.cancelOrder(id, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng đã được hủy thành công");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/user/order-history";
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsServiceImpl.CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }
        return null;
    }
}
