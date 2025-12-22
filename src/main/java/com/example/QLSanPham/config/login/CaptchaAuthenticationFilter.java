package com.example.QLSanPham.config.login;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CaptchaAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("POST".equals(request.getMethod()) && "/login".equals(request.getRequestURI())) {
            HttpSession session = request.getSession();
            Integer failCount = (Integer) session.getAttribute("loginFailCount");
            boolean needCaptcha = failCount != null && failCount >= 3;

            if (needCaptcha) {
                String captchaInput = request.getParameter("captchaInput");
                String captcha = (String) session.getAttribute("captcha");
                if (captcha == null || !captcha.equals(captchaInput)) {
                    // Redirect về login với lỗi captcha
                    response.sendRedirect("/login?captchaError=true");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}