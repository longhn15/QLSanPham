package com.example.QLSanPham.entity.base;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass // Đánh dấu đây là lớp cha, không tạo bảng riêng
@EntityListeners(AuditingEntityListener.class) // Tự động lắng nghe sự kiện save/update
public abstract class BaseEntity {
    // Mỗi entity con sẽ khai báo @Id riêng phù hợp với database

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
