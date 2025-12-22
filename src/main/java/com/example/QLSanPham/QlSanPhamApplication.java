package com.example.QLSanPham;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication // khai báo đây là ứng dụng Spring Boot
@EnableJpaAuditing // tự động cập nhật các trường ngày tạo và ngày sửa đổi
public class QlSanPhamApplication {

	public static void main(String[] args) {
		SpringApplication.run(QlSanPhamApplication.class, args);
	}

}


