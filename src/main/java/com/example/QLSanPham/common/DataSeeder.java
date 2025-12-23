package com.example.QLSanPham.common;


import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.CategoryRepository;
import com.example.QLSanPham.repository.ProductRepository;
import com.example.QLSanPham.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Injection (Thay vì @Autowired) - Chuẩn Clean Code
    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                     ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Tạo tài khoản Admin nếu chưa tồn tại
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456")) // Pass mặc định
                    .fullName("Super Admin")
                    .email("admin@flashsale.com")
                    .phoneNumber("0123456789")
                    .role("ADMIN")
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println(">>> Đã khởi tạo tài khoản ADMIN mẫu (admin/123456)");
        }
        
    }
}