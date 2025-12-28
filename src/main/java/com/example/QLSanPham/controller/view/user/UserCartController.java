package com.example.QLSanPham.controller.view.user;

import com.example.QLSanPham.dto.response.CartResponse;
import com.example.QLSanPham.security.UserDetailsServiceImpl;
import com.example.QLSanPham.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/cart")
@RequiredArgsConstructor
public class UserCartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        
        Long userId = getUserId(userDetails);
        String sessionId = session.getId();

        CartResponse cart = cartService.getCart(userId, sessionId);
        
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("subtotal", cart.getSubtotal());
        model.addAttribute("shippingFee", cart.getShippingFee());
        model.addAttribute("total", cart.getTotal());
        model.addAttribute("totalItems", cart.getTotalItems());
        
        return "user/Cart";
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsServiceImpl.CustomUserDetails) {
            return ((UserDetailsServiceImpl.CustomUserDetails) userDetails).getId();
        }
        return null;
    }
}
