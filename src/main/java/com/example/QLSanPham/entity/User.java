package com.example.QLSanPham.entity;

import jakarta.persistence.*;
import lombok.*;

import com.example.QLSanPham.entity.base.BaseEntity;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String password; // Lưu BCrypt hash

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", length = 15)
    private String phoneNumber;

    @Column(length = 20)
    private String role = "USER"; // ADMIN hoặc USER

    @Column(name = "is_active")
    private boolean isActive = true;
}
