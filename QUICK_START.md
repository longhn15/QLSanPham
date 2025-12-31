# 🚀 Quick Start Guide - Tách Giỏ Hàng & Thanh Toán

## Tóm Tắt Nhanh

Bạn vừa triển khai thành công hệ thống mua hàng 2 bước:

### **Bước 1️⃣: Giỏ Hàng (Cart)** 
- Checkbox để chọn sản phẩm
- Thay đổi số lượng (local only)
- Tóm tắt chỉ hiển thị items được chọn

### **Bước 2️⃣: Thanh Toán (Checkout)**
- Nhập thông tin khách hàng
- Chọn địa chỉ giao hàng
- Chọn phương thức giao/thanh toán
- Xem tóm tắt (read-only)

### **Bước 3️⃣: Lịch Sử (Order History)**
- Xem tất cả đơn hàng
- Theo dõi trạng thái
- Đánh giá sản phẩm

---

## 📂 Files Chính

| File | Type | Mục Đích |
|------|------|----------|
| **Cart.html** | Frontend | Giỏ hàng với checkbox |
| **Checkout.html** | Frontend | Form thanh toán |
| **OrderHistory.html** | Frontend | Lịch sử mua hàng |
| **OrderApiController.java** | Backend | API `/api/order/create` |
| **OrderService.java** | Backend | Logic tạo order |
| **CreateOrderRequest.java** | DTO | Request format |
| **OrderResponse.java** | DTO | Response format |

---

## 🔄 Data Flow

```
Cart (Select items)
    ↓
    └─ Store in localStorage
    ↓
Checkout (Get from localStorage)
    ↓
    ├─ Fill customer form
    ├─ Choose shipping
    ├─ Choose payment
    ↓
    └─ POST /api/order/create
       ↓
       ├─ Create Order in DB
       ├─ Create OrderDetails
       ├─ Delete cart items
       ↓
       └─ Return OrderResponse
           ↓
           └─ Redirect to Order History
               ├─ Display order
               ├─ Show status badge
               └─ Show success toast
```

---

## 🧪 Test URLs

| Step | URL | Expected |
|------|-----|----------|
| 1 | `http://localhost:8080/user/cart` | Giỏ hàng + checkboxes |
| 2 | Select & click "Thanh Toán" | → `/user/checkout` |
| 3 | Fill form & click "Đặt hàng" | → `/user/order-history` |
| 4 | View order | Hiển thị chi tiết đơn |

---

## 💾 Database Info

Dữ liệu được lưu trong 2 bảng:

**Orders Table:**
- order_id, user_id, total_amount, status, payment_method, shipping_address, note, order_date

**OrderDetails Table:**
- order_detail_id, order_id, product_id, quantity, price_at_purchase

---

## 🎯 Key Features

✅ **Giỏ Hàng (Cart)**
- Select/Deselect items
- Update quantity locally
- Preview selected total
- Deferral updates until checkout

✅ **Thanh Toán (Checkout)**
- Customer info form
- Address form
- Shipping method selection
- Payment method selection
- Order notes
- Read-only summary

✅ **Lịch Sử (Order History)**
- Paginated order list
- Status color coding
- Order details
- Action buttons
- Success notification for new orders

---

## 🔗 API Endpoint

### Create Order
```
POST /api/order/create
Content-Type: application/json

{
  "fullName": "Nguyen Van A",
  "phone": "0123456789",
  "email": "user@example.com",
  "shippingAddress": "123 Đường ABC, Quận 1, Hà Nội",
  "paymentMethod": "cod",
  "shippingMethod": "standard",
  "note": "Giao vào buổi sáng",
  "cartItems": [
    {
      "id": 1,
      "quantity": 2,
      "price": 100000
    }
  ]
}

Response:
{
  "success": true,
  "message": "Đơn hàng được tạo thành công!",
  "data": {
    "orderId": 123,
    "orderNumber": "ORD-123-1234567890",
    "totalAmount": 230000,
    "status": "PENDING",
    "paymentMethod": "cod",
    "shippingAddress": "123 Đường ABC, Quận 1, Hà Nội",
    "orderDate": "2024-01-15T10:30:00"
  }
}
```

---

## ⚙️ Configuration

**Shipping Costs:**
- Standard: 30,000 ₫
- Express: 60,000 ₫

**Order Status:**
- PENDING: 💛 Chờ xác nhận
- CONFIRMED: 🔵 Đã xác nhận
- SHIPPED: 🟣 Đang giao
- DELIVERED: 🟢 Đã giao
- CANCELLED: 🔴 Đã hủy

---

## 📱 Mobile Responsive

Tất cả pages responsive trên:
- Desktop
- Tablet
- Mobile (< 640px)

---

## 🐛 Troubleshooting

**Q: Checkout không load sản phẩm?**
A: Check localStorage "selectedCartItems" - phải có dữ liệu

**Q: Order không được tạo?**
A: Kiểm tra console error, validate form fields

**Q: Số lượng không update?**
A: Bình thường, chỉ update khi bấm Thanh Toán

**Q: Cart items không xóa?**
A: Check API response, có thể lỗi khi remove

---

## 📞 Support

Nếu có vấn đề, check:
1. Server logs
2. Browser console
3. Network tab (F12)
4. Database queries

---

**Chúc bạn triển khai thành công! 🎉**
