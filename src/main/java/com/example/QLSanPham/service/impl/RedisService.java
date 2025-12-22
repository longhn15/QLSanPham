// package com.example.QLSanPham.service.impl;


// import java.util.concurrent.TimeUnit;

// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.stereotype.Service;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class RedisService {

//     private final RedisTemplate<String, Object> redisTemplate;

//     // Lưu cache với thời gian hết hạn (TTL)
//     public void set(String key, Object value, long timeout, TimeUnit unit) {
//         redisTemplate.opsForValue().set(key, value, timeout, unit);
//     }

//     public Object get(String key) {
//         return redisTemplate.opsForValue().get(key);
//     }

//     // Xóa cache
//     public void delete(String key) {
//         redisTemplate.delete(key);
//     }

//     // Kiểm tra hàng tồn kho trong Redis (Atomic Operation) - Điểm ăn tiền
//     // Trả về false nếu hết hàng
//     public boolean decrementStock(String key) {
//         Long remainingStock = redisTemplate.opsForValue().decrement(key);
//         if (remainingStock != null && remainingStock < 0) {
//             // Nếu trừ xong mà âm -> Hết hàng -> Cộng lại ngay (Hoàn tác)
//             redisTemplate.opsForValue().increment(key);
//             return false;
//         }
//         return true;
//     }
    
//     // Set lại kho (dùng khi Admin cập nhật hoặc khởi tạo Flash Sale)
//     public void setStock(String key, int quantity) {
//         redisTemplate.opsForValue().set(key, quantity);
//     }
// }