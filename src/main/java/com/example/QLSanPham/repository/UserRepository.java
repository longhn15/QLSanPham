package com.example.QLSanPham.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.QLSanPham.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Dùng Optional để tránh NullPointerException khi code Service
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);

    // Kiểm tra tồn tại cực nhanh (trả về boolean thay vì bê cả entity lên)
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
