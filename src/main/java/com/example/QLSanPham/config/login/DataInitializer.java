// package com.example.QLSanPham.config.login;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;

// import com.example.QLSanPham.entity.User;
// import com.example.QLSanPham.service.impl.UserService;

// import jakarta.annotation.PostConstruct;

// @Component
// public class DataInitializer {

//     @Autowired
//     private UserService userSV;

//     @PostConstruct
//     public void init() {
//         // Kiểm tra xem tài khoản admin đã tồn tại chưa
//         if (userSV.findByUsername("admin").isEmpty()) {
//             // Tạo tài khoản admin với mật khẩu đã mã hóa
//             User admin = new User();
//             admin.setFullName("admin");
//             admin.setPassword("admin"); // Mật khẩu thô, sẽ được mã hóa trong register
//             User user = userSV.register(admin);
//             System.out.println("Tài khoản admin đã được tạo với mật khẩu mã hóa với username: " + user.getUsername());
//         }
//     }
// }