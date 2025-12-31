package com.example.QLSanPham.controller.view.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.UserRepository;
import com.example.QLSanPham.security.UserDetailsServiceImpl;

@Controller
@RequestMapping("/user/checkout")
public class UserCheckoutController {

    @Autowired
    private UserRepository userRepository;

    // @Autowired
    // private CartService cartService;

    // @Autowired
    // private CartItemRepository cartItemRepository;

    @GetMapping
    public String viewCheckout(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserId(userDetails);
        
        if (userId != null && userId > 0) {
            // Lấy thông tin người dùng để điền sẵn
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                model.addAttribute("user", user);
                model.addAttribute("userId", userId);
                model.addAttribute("userFullName", user.getFullName());
                model.addAttribute("userPhone", user.getPhoneNumber());
                model.addAttribute("userEmail", user.getEmail());
            }
        }
        
        return "user/Checkout";
    }

    private Long getUserId(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsServiceImpl.CustomUserDetails customUser) {
            return customUser.getId();
        }
        return null;
    }
}
