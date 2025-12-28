package com.example.QLSanPham.config.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.QLSanPham.repository.UserRepository;
import com.example.QLSanPham.service.CartService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CartService cartService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();
        
        // Lưu sessionId CŨ trước khi Spring Security có thể thay đổi nó
        final String oldSessionId = (String) session.getAttribute("CART_SESSION_ID");
        final String actualSessionId = (oldSessionId != null) ? oldSessionId : session.getId();
        
        session.removeAttribute("loginFailCount");
        session.removeAttribute("captcha");
        session.removeAttribute("CART_SESSION_ID"); // Clear temp storage

        // Merge cart từ session sang user khi đăng nhập
        String username = authentication.getName();
        
        userRepository.findByUsername(username).ifPresent(user -> {
            try {
                System.out.println("=== CART MERGE DEBUG ===");
                System.out.println("Username: " + username);
                System.out.println("User ID: " + user.getId());
                System.out.println("Old Session ID: " + actualSessionId);
                System.out.println("Current Session ID: " + session.getId());
                
                cartService.mergeSessionCartToUser(actualSessionId, user.getId());
                Integer count = cartService.getCartItemCount(user.getId(), session.getId());
                session.setAttribute("CART_COUNT", count);

                System.out.println("Cart merge completed successfully, new count: " + count);
            } catch (Exception e) {
                // Log error but don't break login flow
                System.err.println("Error merging cart: " + e.getMessage());
                e.printStackTrace();
            }
        });

        // Lấy role từ authorities
        String redirectUrl = "/";
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            String role = authority.getAuthority();
            
            // Nếu là ADMIN thì redirect về dashboard admin
            if (role.equals("ADMIN") || role.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            }
            // Nếu là USER thì redirect về trang chủ user
            else if (role.equals("USER") || role.equals("ROLE_USER")) {
                redirectUrl = "/user/home";
                break;
            }
        }

        response.sendRedirect(redirectUrl);
    }
}
