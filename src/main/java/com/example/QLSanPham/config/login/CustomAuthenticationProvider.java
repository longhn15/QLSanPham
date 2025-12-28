package com.example.QLSanPham.config.login;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.security.UserDetailsServiceImpl;
import com.example.QLSanPham.service.impl.UserService;


public class CustomAuthenticationProvider implements AuthenticationProvider {
    
    private final UserService userSV;

    public CustomAuthenticationProvider(UserService userSV) {
        this.userSV = userSV;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Tìm user từ database
        Optional<User> userOpt = userSV.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Tài khoản không tồn tại");
        }

        User user = userOpt.get();
        
        // Kiểm tra user có active không
        if (!user.isActive()) {
            throw new BadCredentialsException("Tài khoản này đã bị khóa");
        }

        // Kiểm tra password
        boolean isValid = userSV.checkLogin(username, password);
        if (!isValid) {
            throw new BadCredentialsException("Sai tài khoản hoặc mật khẩu");
        }

        // Chuyển role thành GrantedAuthority
        var authorities = user.getRole() != null && !user.getRole().isEmpty()
            ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // Use CustomUserDetails so we keep user ID in SecurityContext (needed for cart lookup)
        UserDetails principal = new UserDetailsServiceImpl.CustomUserDetails(user);

        // Do not store raw password in the authentication
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
    
}
