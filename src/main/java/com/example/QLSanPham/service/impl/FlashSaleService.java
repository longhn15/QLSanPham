// package com.example.QLSanPham.service.impl;


// import com.example.QLSanPham.entity.FlashSaleItem;
// import com.example.QLSanPham.entity.Order;
// import com.example.QLSanPham.entity.User;
// import com.example.QLSanPham.repository.FlashSaleItemRepository;
// import com.example.QLSanPham.repository.OrderRepository;
// import com.example.QLSanPham.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// @Service
// @RequiredArgsConstructor
// @Slf4j
// public class FlashSaleService {

//     private final FlashSaleItemRepository flashSaleItemRepository;
//     private final RedisService redisService;
//     private final OrderService orderService; // Gọi service khác để tạo đơn
    
//     private static final String STOCK_KEY_PREFIX = "fs:stock:";

//     /**
//      * ADMIN: Kích hoạt Flash Sale
//      * Logic: Load tồn kho từ DB lên Redis để chuẩn bị chiến
//      */
//     @Transactional
//     public void activateFlashSaleSession(Long sessionId) {
//         // ... Logic update status session thành ACTIVE ...
        
//         // Load stock lên Redis
//         var items = flashSaleItemRepository.findByFlashSaleSessionIdAndStatusTrue(sessionId, null);
//         for (FlashSaleItem item : items) {
//             String key = STOCK_KEY_PREFIX + item.getId();
//             redisService.setStock(key, item.getOriginalStock() - item.getSoldQuantity());
//             log.info("Loaded stock to Redis: {} -> {}", key, item.getOriginalStock());
//         }
//     }

//     /**
//      * USER: Mua hàng Flash Sale (Logic chịu tải cao)
//      * Flow: Check Redis -> Nếu còn -> Đẩy DB (hoặc Queue)
//      */
//     @Transactional
//     public boolean buyFlashSaleItem(Long userId, Long flashSaleItemId) {
//         String key = STOCK_KEY_PREFIX + flashSaleItemId;

//         // 1. CHẶN Ở CẤP REDIS (Nhanh nhất)
//         boolean stockAvailable = redisService.decrementStock(key);
//         if (!stockAvailable) {
//             log.warn("User {} mua thất bại: Hết hàng trong Redis", userId);
//             throw new RuntimeException("Sản phẩm đã hết hàng!"); // Trả về lỗi đẹp cho Controller
//         }

//         // 2. NẾU QUA CỬA REDIS -> XỬ LÝ DB (Đã giảm tải được 99% request rác)
//         try {
//             // Lock dòng dữ liệu trong DB (Optimistic Lock @Version tự lo)
//             FlashSaleItem item = flashSaleItemRepository.findById(flashSaleItemId)
//                     .orElseThrow(() -> new RuntimeException("Item không tồn tại"));

//             // Check lại DB cho chắc chắn (Double check)
//             if (item.getSoldQuantity() >= item.getOriginalStock()) {
//                 // Rollback Redis nếu DB thực tế đã hết (dù hiếm khi xảy ra)
//                 redisService.setStock(key, 0); 
//                 throw new RuntimeException("Đã hết hàng!");
//             }

//             // Tăng số lượng đã bán
//             item.setSoldQuantity(item.getSoldQuantity() + 1);
//             flashSaleItemRepository.save(item);

//             // 3. TẠO ĐƠN HÀNG
//             orderService.createFlashSaleOrder(userId, item);
            
//             return true;

//         } catch (Exception e) {
//             // Nếu lỗi DB -> Phải hoàn lại kho Redis (Compensation)
//             // redisTemplate.opsForValue().increment(key); -> Cần logic này nếu muốn chính xác tuyệt đối
//             throw e; 
//         }
//     }
// }