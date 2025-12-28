package com.example.QLSanPham.config.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.QLSanPham.entity.User;
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
        session.removeAttribute("loginFailCount");
        session.removeAttribute("captcha");

        // Merge cart từ session sang user khi đăng nhập
        String username = authentication.getName();
        String sessionId = session.getId();
        
        userRepository.findByUsername(username).ifPresent(user -> {
            try {
                cartService.mergeSessionCartToUser(sessionId, user.getId());
            } catch (Exception e) {
                // Log error but don't break login flow
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
