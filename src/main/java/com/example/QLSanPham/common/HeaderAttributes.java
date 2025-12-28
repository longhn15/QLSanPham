package com.example.QLSanPham.common;

import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.UserRepository;
import com.example.QLSanPham.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice // Apply model attributes to all controllers/views
@RequiredArgsConstructor
public class HeaderAttributes {

    private final UserRepository userRepository;
    private final CartService cartService;

    @ModelAttribute
    public void addHeaderAttributes(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        String username = null;
        String email = null;
        // String avatarUrl = null;

        System.out.println("=== HEADER ATTRIBUTES DEBUG ===");
        System.out.println("Auth: " + auth);
        System.out.println("Auth name: " + (auth != null ? auth.getName() : "null"));
        System.out.println("Is authenticated: " + (auth != null ? auth.isAuthenticated() : "false"));

        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            username = auth.getName();
            System.out.println("Username found: " + username);
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userId = user.getId();
                email = user.getEmail();
                System.out.println("User loaded - ID: " + userId + ", Email: " + email);
            } else {
                System.out.println("User not found in database for username: " + username);
            }
        } else {
            System.out.println("User not authenticated or anonymous");
        }

        String sessionId = session.getId();
        
        // Store session ID for cart merge later
        if (userId == null && session.getAttribute("CART_SESSION_ID") == null) {
            session.setAttribute("CART_SESSION_ID", sessionId);
        }

        // Prefer cached cart count in session to avoid recomputing on every view render
        Integer cartCount = (Integer) session.getAttribute("CART_COUNT");
        if (cartCount == null) {
            cartCount = cartService.getCartItemCount(userId, sessionId);
            session.setAttribute("CART_COUNT", cartCount);
        }

        System.out.println("Setting model attributes - username: " + username + ", cartCount: " + cartCount);

        model.addAttribute("username", username);
        model.addAttribute("email", email);
        // model.addAttribute("avatarUrl", avatarUrl);
        model.addAttribute("cartCount", cartCount);
    }
}
