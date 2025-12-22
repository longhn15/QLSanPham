// package com.example.QLSanPham.controller.api;


// import org.springframework.messaging.handler.annotation.MessageMapping;
// import org.springframework.messaging.handler.annotation.SendTo;
// import org.springframework.stereotype.Controller;

// @Controller
// public class WebSocketController {

//     // Client gửi message lên /app/stock-update
//     // Server xử lý và bắn về /topic/public
//     @MessageMapping("/stock-update")
//     @SendTo("/topic/public")
//     public String broadcastStockUpdate(String message) {
//         // Message dạng: "iPhone 15 - Chỉ còn 5 cái"
//         return message; 
//     }
    
//     // Logic thực tế: Bạn sẽ Inject SimpMessagingTemplate vào FlashSaleService
//     // Mỗi khi có người mua thành công -> Bắn event update xuống topic này.
// }