// package com.example.QLSanPham.controller.api;

// // importcom.example.QLSanPham.dto.ApiResponse; // Class wrapper trả về JSON chuẩn: {status, message, data}
// // importcom.example.QLSanPham.service.FlashSaleService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.web.bind.annotation.*;

// import org.springframework.web.bind.annotation.PostMapping;

// @RestController
// @RequestMapping("/api/flash-sale")
// @RequiredArgsConstructor
// public class FlashSaleApiController {

//     private final FlashSaleService flashSaleService;

//     /**
//      * API Mua hàng Flash Sale
//      * Method: POST
//      * URL: /api/flash-sale/buy/{itemId}
//      * Header: Authorization: Bearer ... (hoặc Session Cookie)
//      */
//     @PostMapping("/buy/{itemId}")
//     public ResponseEntity<?> buyFlashSaleItem(@PathVariable Long itemId,
//                                               @AuthenticationPrincipal UserDetails userDetails) {
//         if (userDetails == null) {
//             return ResponseEntity.status(401).body(new ApiResponse(false, "Vui lòng đăng nhập để săn sale!"));
//         }

//         try {
//             // Gọi Service xử lý (Redis -> DB)
//             // Cần lấy UserId từ username (userDetails.getUsername())
//             boolean success = flashSaleService.buyFlashSaleItem(userDetails.getUsername(), itemId);
            
//             if (success) {
//                 return ResponseEntity.ok(new ApiResponse(true, "Chúc mừng! Bạn đã săn được hàng. Vui lòng thanh toán."));
//             } else {
//                 return ResponseEntity.badRequest().body(new ApiResponse(false, "Rất tiếc, sản phẩm đã hết hàng!"));
//             }
//         } catch (Exception e) {
//             // Bắt lỗi như "Hết hàng" hoặc "Mua quá giới hạn"
//             return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
//         }
//     }
// }
