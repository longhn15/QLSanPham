// package com.example.QLSanPham.service.impl;


// import com.example.QLSanPham.entity.*;
// import com.example.QLSanPham.repository.OrderRepository;
// import com.example.QLSanPham.repository.UserRepository;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;


// @Service
// @RequiredArgsConstructor
// public class OrderService {

//     private final OrderRepository orderRepository;
//     private final UserRepository userRepository;

//     @Transactional
//     public Order creatOrder(Long userId, FlashSaleItem item) {
//         User user = userRepository.findById(userId)
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         // Tạo đơn hàng
//         Order order = Order.builder()
//                 .user(user)
//                 .status(status) // Chờ thanh toán
//                 .totalAmount(item.getFlashSalePrice()) // Giá sale
//                 .paymentMethod("COD") // Mặc định
//                 .build();

//         // Tạo chi tiết đơn
//         OrderDetail detail = OrderDetail.builder()
//                 .product(item.getProduct())
//                 .flashSaleItem(item) // Gắn mác item flash sale
//                 .quantity(1) // Flash sale thường giới hạn mua 1
//                 .priceAtPurchase(item.getFlashSalePrice())
//                 .build();

//         order.addDetail(detail);

//         return orderRepository.save(order);
//     }
// }