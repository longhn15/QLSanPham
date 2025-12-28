package com.example.QLSanPham.config.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.QLSanPham.service.impl.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userSV;

    @Autowired
    private CaptchaAuthenticationFilter captchaAuthenticationFilter;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt tạm thời để test API dễ hơn (Bật lại sau nếu cần)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/user/home/**",
                    "/user/product/**",
                    "/user/cart/**",
                    "/api/cart/**",
                    "/login",
                    "/register",
                    "/login/captcha",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                )
                .permitAll()
                // Phân quyền: Chỉ ADMIN có thể vào /admin/**
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Phân quyền: USER có thể vào /user/**
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .authenticationProvider(new CustomAuthenticationProvider(userSV))
            .sessionManagement(session -> session
                .sessionFixation().migrateSession() // Migrate session instead of creating new one
            )
            .formLogin(form -> form
                .loginPage("/login") // Trang login custom của bạn
                // .defaultSuccessUrl("/", true) // Login xong về trang chủ
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler(new CustomAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Xử lý khi User thường cố vào trang Admin
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"))
            .addFilterBefore(captchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

// SecurityConfig.java: Phân quyền. Quan trọng: Tách biệt cấu hình cho /admin/** 
// (cần role ADMIN) và /flash-sale/** (cần login).