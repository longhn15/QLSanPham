package com.example.QLSanPham.config.login;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        HttpSession session = request.getSession();
        session.removeAttribute("loginFailCount");
        session.removeAttribute("captcha");

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
