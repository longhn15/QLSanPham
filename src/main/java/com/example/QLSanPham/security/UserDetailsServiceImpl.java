package com.example.QLSanPham.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.UserRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;

/**
 * UserDetailsService implementation for Spring Security
 * Loads user-specific data for authentication
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        return new CustomUserDetails(user);
    }

    /**
     * Custom UserDetails implementation that wraps our User entity
     * and provides additional user information like ID
     */
    @Getter
    public static class CustomUserDetails implements UserDetails {
        private final Long id;
        private final String username;
        private final String password;
        private final String email;
        private final String fullName;
        private final boolean active;
        private final Collection<? extends GrantedAuthority> authorities;

        public CustomUserDetails(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.email = user.getEmail();
            this.fullName = user.getFullName();
            this.active = user.isActive();
            
            // Convert single role to GrantedAuthority
            String role = user.getRole() != null && !user.getRole().isEmpty() 
                    ? user.getRole() 
                    : "USER";
            
            // Thêm prefix ROLE_ nếu chưa có
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            
            this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return active;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return active;
        }
    }
}
