package com.example.QLSanPham.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.QLSanPham.dto.request.RegisterRequestDTO;
import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public void registerUser(RegisterRequestDTO dto) {
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .fullName(null) // Form đăng ký nên thêm trường này
                .password(passwordEncoder.encode(dto.getPassword()))
                
                // 2. CÁC TRƯỜNG HỆ THỐNG TỰ ĐIỀN (QUAN TRỌNG)
                .role("USER") // Mặc định là User thường
                .isActive(true) // Mặc định kích hoạt ngay
                
                // 3. CÁC TRƯỜNG CÒN THIẾU ĐỂ NULL HOẶC EMPTY
                .phoneNumber(null) 
                .build();

        userRepository.save(user);
    }

    public boolean checkLogin(String username, String rawPassword) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            // So sánh mật khẩu đã mã hóa
            return passwordEncoder.matches(rawPassword, user.getPassword());
        }
        return false;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
