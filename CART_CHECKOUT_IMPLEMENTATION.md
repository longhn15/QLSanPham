# 🛒 Tách Giỏ Hàng và Thanh Toán - Hướng Dẫn Triển Khai Toàn Diện

## 📋 Tóm tắt Thay đổi

Đã hoàn thành việc tách giỏ hàng và thanh toán thành 2 quy trình riêng biệt với chức năng chọn sản phẩm, cập nhật bị hoãn, và tạo đơn hàng.

---

## 🎯 Tính năng Chính

### 1. **Giỏ Hàng Mới (Cart Page)**
- ✅ Checkbox trước mỗi sản phẩm để lựa chọn
- ✅ Chỉnh sửa số lượng cục bộ (không cập nhật database liền)
- ✅ Chỉ cập nhật khi bấm "Thanh toán"
- ✅ Tóm tắt đơn hàng chỉ hiển thị các item được chọn
- ✅ Item được chọn được đánh dấu với viền cam và nền cam nhạt

### 2. **Trang Thanh Toán Mới (Checkout Page)**
- ✅ Đọc dữ liệu sản phẩm đã chọn từ localStorage
- ✅ Form thông tin khách hàng (Họ tên, Điện thoại, Email)
- ✅ Form địa chỉ giao hàng (Địa chỉ chi tiết, Tỉnh/Thành phố, Quận/Huyện)
- ✅ Lựa chọn phương thức giao hàng (Chuẩn 30k, Nhanh 60k)
- ✅ Lựa chọn phương thức thanh toán (COD, Chuyển khoản)
- ✅ Ghi chú đơn hàng (tuỳ chọn)
- ✅ Tóm tắt chỉ đọc các sản phẩm đã chọn
- ✅ Nút quay lại giỏ hàng để sửa đổi
- ✅ Nút đặt hàng để hoàn tất

### 3. **Quy Trình Đặt Hàng**
- ✅ POST `/api/order/create` - Tạo đơn hàng
- ✅ Lưu Order và OrderDetails vào database
- ✅ Xóa các item giỏ hàng đã mua
- ✅ Trả về thông tin đơn hàng và orderId
- ✅ Chuyển hướng tới trang lịch sử mua hàng

### 4. **Trang Lịch Sử Mua Hàng (Order History)**
- ✅ Danh sách tất cả đơn hàng của người dùng
- ✅ Hiển thị chi tiết đơn hàng (Mã, Ngày, Trạng thái, Tổng tiền)
- ✅ Địa chỉ giao hàng
- ✅ Danh sách sản phẩm trong đơn
- ✅ Thông báo thành công cho đơn hàng mới (highlight)
- ✅ Trạng thái đơn hàng được mã hoá màu
- ✅ Nút quay lại trang chủ

---

## 📁 Danh Sách File Được Tạo/Sửa

### **Frontend - Templates (HTML)**

1. **[Cart.html](src/main/resources/templates/user/Cart.html)** - ✏️ Sửa
   - Thêm checkbox selection cho mỗi item
   - Thêm `th:data-selected="false"` attribute
   - Thêm hàm `toggleItemSelection(cartItemId)`
   - Thêm hàm `updateSelectedItemsSummary()`
   - Thêm hàm `proceedToCheckout()`
   - Lưu selected items vào localStorage
   - Cập nhật UI khi item được chọn (viền cam, nền cam)
   - Thay đổi `updateQuantity()` để không gửi API liền

2. **[Checkout.html](src/main/resources/templates/user/Checkout.html)** - ✏️ Sửa toàn bộ
   - Tạo mới layout hoàn chỉnh với form khách hàng
   - Form thông tin (Họ tên, Điện thoại, Email)
   - Form địa chỉ giao hàng
   - Lựa chọn phương thức giao hàng
   - Lựa chọn phương thức thanh toán
   - Ghi chú đơn hàng
   - Tóm tắt đơn hàng (read-only)
   - JavaScript để load sản phẩm từ localStorage
   - Hàm `submitOrder()` để tạo đơn hàng

3. **[OrderHistory.html](src/main/resources/templates/user/OrderHistory.html)** - ✨ Mới
   - Danh sách tất cả đơn hàng của người dùng
   - Hiển thị mã đơn, ngày, trạng thái, tổng tiền
   - Địa chỉ giao hàng
   - Danh sách sản phẩm của mỗi đơn
   - Hiển thị trạng thái với màu sắc
   - Thông báo thành công cho đơn mới
   - Các nút hành động (Chi tiết, Hủy, Đánh giá)

---

### **Backend - Controllers (Java)**

1. **[OrderApiController.java](src/main/java/com/example/QLSanPham/controller/api/OrderApiController.java)** - ✨ Mới
   - POST `/api/order/create` - Tạo đơn hàng từ selected cart items
   - Xử lý validation từ CreateOrderRequest
   - Gọi `orderService.createOrder()`
   - Trả về OrderResponse với orderId

2. **[UserOrderHistoryController.java](src/main/java/com/example/QLSanPham/controller/view/user/UserOrderHistoryController.java)** - ✨ Mới
   - GET `/user/order-history` - Hiển thị lịch sử đơn hàng
   - Hỗ trợ phân trang (page parameter)
   - Hỗ trợ highlight đơn hàng mới (orderId parameter)
   - Lấy dữ liệu từ `orderService.getUserOrders()`

---

### **Backend - Services (Java)**

1. **[OrderService.java](src/main/java/com/example/QLSanPham/service/impl/OrderService.java)** - ✏️ Sửa toàn bộ
   - `createOrder(CreateOrderRequest, userId, sessionId)` - Tạo Order
     - Validate user
     - Tính tổng tiền từ cart items + shipping
     - Tạo Order entity với các OrderDetails
     - Lưu vào database
     - Xóa cart items đã mua
     - Trả về OrderResponse
   - `getUserOrders(userId, pageable)` - Lấy danh sách đơn hàng
     - Gọi `orderRepository.findByUserIdOrderByCreatedAtDesc()`
     - Trả về Page<Order>

---

### **Backend - DTOs (Java)**

1. **[CreateOrderRequest.java](src/main/java/com/example/QLSanPham/dto/request/CreateOrderRequest.java)** - ✨ Mới
   ```java
   - fullName: String (required)
   - phone: String (required)
   - email: String
   - shippingAddress: String (required)
   - paymentMethod: String (required) - cod/bank
   - shippingMethod: String - standard/express
   - note: String
   - cartItems: List<OrderItemRequest>
     - id: Long (cart item id)
     - quantity: Integer
     - price: Double
   ```

2. **[OrderResponse.java](src/main/java/com/example/QLSanPham/dto/response/OrderResponse.java)** - ✨ Mới
   ```java
   - orderId: Long
   - orderNumber: String
   - totalAmount: BigDecimal
   - status: String
   - paymentMethod: String
   - shippingAddress: String
   - orderDate: LocalDateTime
   - note: String
   ```

---

### **Backend - Entities (Java)**

1. **[OrderStatus.java](src/main/java/com/example/QLSanPham/entity/OrderStatus.java)** - ✨ Mới (Enum)
   ```java
   PENDING      // Chờ xác nhận
   CONFIRMED    // Đã xác nhận
   SHIPPED      // Đang giao
   DELIVERED    // Đã giao
   CANCELLED    // Đã hủy
   ```

2. **[Order.java](src/main/java/com/example/QLSanPham/entity/Order.java)** - Không thay đổi (đã có sẵn)
3. **[OrderDetail.java](src/main/java/com/example/QLSanPham/entity/OrderDetail.java)** - ✏️ Sửa nhỏ
   - Xóa enum OrderStatus (moved to separate file)

---

## 🔄 Quy Trình Sử Dụng

### **User Flow:**

```
1. Người dùng xem Giỏ Hàng
   └─ Các sản phẩm hiển thị với checkbox
   └─ Có thể thay đổi số lượng (chỉ local, chưa save)
   └─ Chọn những sản phẩm muốn mua
   └─ Tóm tắt chỉ hiển thị items được chọn

2. Bấm "Tiến hành thanh toán"
   └─ Validate: Phải chọn ít nhất 1 sản phẩm
   └─ Lưu selected items vào localStorage
   └─ Chuyển hướng đến `/user/checkout`

3. Ở Trang Thanh Toán
   └─ Điền thông tin khách hàng
   └─ Chọn địa chỉ giao hàng
   └─ Chọn phương thức giao hàng (chuẩn/nhanh)
   └─ Chọn phương thức thanh toán (COD/Bank)
   └─ Có thể thêm ghi chú
   └─ Tóm tắt hiển thị read-only các sản phẩm

4. Bấm "Đặt đơn hàng"
   └─ POST `/api/order/create` với toàn bộ dữ liệu
   └─ Server:
      ├─ Tạo Order
      ├─ Tạo OrderDetails
      ├─ Lưu vào database
      └─ Xóa cart items

5. Nếu thành công
   └─ Thông báo "Đặt hàng thành công!"
   └─ Clear localStorage
   └─ Chuyển hướng `/user/order-history?orderId=123`

6. Ở Trang Lịch Sử
   └─ Hiển thị tất cả đơn hàng
   └─ Highlight đơn hàng mới (nếu có orderId param)
   └─ Thông báo thành công tự ẩn sau 5s
```

---

## 🔧 Các Thay Đổi Chi Tiết

### **Cart.html - JavaScript Changes**

#### New Functions:

1. **`toggleItemSelection(cartItemId)`**
   - Toggle checkbox state
   - Cập nhật `data-selected` attribute
   - Thay đổi UI (border + background color)
   - Gọi `updateSelectedItemsSummary()`

2. **`updateSelectedItemsSummary()`**
   - Lấy tất cả selected checkboxes
   - Tính subtotal từ selected items
   - Tính shipping fee (30k hoặc 0)
   - Cập nhật summary sidebar
   - Enable/disable checkout button

3. **`proceedToCheckout()`**
   - Validate: Có item được chọn?
   - Tạo array với {id, quantity, price}
   - Lưu vào `localStorage.selectedCartItems`
   - Navigate tới `/user/checkout`

4. **Modified `updateQuantity(cartItemId, newQuantity)`**
   - Không gọi API `/api/cart/update` liền
   - Chỉ update `data-quantity` attribute
   - Update DOM display
   - Gọi `updateSelectedItemsSummary()`
   - Show toast "Sẽ cập nhật khi thanh toán"

#### New HTML:

```html
<!-- Checkbox trước mỗi item -->
<input type="checkbox" 
       class="item-checkbox"
       th:onchange="'toggleItemSelection(' + ${item.id} + ')'">

<!-- Nút Thanh Toán thay đổi -->
<button onclick="proceedToCheckout()">...</button>
```

---

### **Checkout.html - Hoàn Toàn Mới**

#### Key Features:

1. **Customer Form** - Họ tên, Điện thoại, Email
2. **Address Form** - Địa chỉ, Tỉnh/Thành phố, Quận/Huyện
3. **Shipping Method** - Standard (30k) / Express (60k)
4. **Payment Method** - COD (enabled) / Bank (disabled)
5. **Order Note** - Ghi chú tuỳ chọn
6. **Summary Sidebar** - Read-only danh sách sản phẩm
7. **Total Calculation** - Subtotal + Shipping = Total

#### Key Functions:

1. **`loadSelectedItems()`**
   - Lấy dữ liệu từ `localStorage.selectedCartItems`
   - Nếu rỗng → Show error → Redirect to cart

2. **`renderOrderItems(items)`**
   - Render mỗi item trong summary
   - Hiển thị quantity × price = total
   - Tính subtotal

3. **`updateSummary(subtotal)`**
   - Tính shipping cost theo method
   - Tính total = subtotal + shipping
   - Update DOM

4. **`submitOrder()`**
   - Validate form (required fields)
   - Lấy payment/shipping methods
   - POST `/api/order/create`
   - Nếu success:
     - Show success toast
     - Clear localStorage
     - Redirect to `/user/order-history?orderId=X`
   - Nếu error: Show error toast, enable button

---

### **OrderHistory.html - Mới Hoàn Toàn**

#### Display Info:

- **Mã Đơn Hàng**: ORD-{id}
- **Ngày Đặt**: Formatted date/time
- **Trạng Thái**: PENDING/CONFIRMED/SHIPPED/DELIVERED/CANCELLED (với màu sắc)
- **Tổng Tiền**: Formatted currency
- **Phương Thức**: COD / Chuyển khoản
- **Địa Chỉ**: shipping_address
- **Sản Phẩm**: Danh sách với {name, quantity, price}

#### Features:

- Empty state khi không có đơn
- Success toast khi có newOrderId parameter
- Status badges với icons và màu sắc
- Action buttons: Chi tiết, Hủy (nếu PENDING), Đánh giá (nếu DELIVERED)
- Pagination buttons (placeholder)

---

## 🚀 Cách Triển Khai

### **1. Backend Setup**

```bash
# Đảm bảo các dependencies đã có:
- spring-data-jpa
- spring-boot-starter-web
- spring-boot-starter-validation
- lombok
- jakarta.persistence

# Build project
mvn clean install

# Chạy server
mvn spring-boot:run
```

### **2. Database**

Các bảng cần thiết:

```sql
-- Nên đã tồn tại, nếu chưa:
CREATE TABLE Orders (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    total_amount DECIMAL(18,2),
    status VARCHAR(20),
    payment_method VARCHAR(50),
    shipping_address VARCHAR(255),
    note VARCHAR(500),
    order_date TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE OrderDetails (
    order_detail_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    price_at_purchase DECIMAL(18,2),
    created_at TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);
```

### **3. Frontend Testing**

**Test Case 1: Select & Deselect**
```
1. Mở /user/cart
2. Bấm checkbox item 1 → Border cam, nền cam
3. Tóm tắt cập nhật chỉ hiển thị item 1
4. Bấm lại → Deselect
```

**Test Case 2: Quantity Change**
```
1. Select item 1 (qty = 1)
2. Tăng qty lên 2 → Toast "Sẽ cập nhật khi thanh toán"
3. Tóm tắt cập nhật giá (không gọi API)
4. Bấm Thanh Toán
5. Ở checkout, item hiển thị qty = 2
```

**Test Case 3: Checkout & Order**
```
1. Select items
2. Bấm Thanh Toán
3. Điền form, chọn phương thức
4. Bấm Đặt Đơn Hàng
5. Redirect đến /user/order-history
6. Đơn hàng hiển thị trong list
7. Cart items xóa
```

---

## ⚠️ Lưu Ý Quan Trọng

1. **localStorage Persistence**
   - Selected items lưu trong localStorage
   - Nếu user refresh checkout page, dữ liệu vẫn có
   - Clear sau khi order success

2. **Quantity Updates**
   - Chỉ local, không hit API
   - Khi bấm Thanh Toán, số lượng từ DOM được gửi

3. **Cart Items Deletion**
   - Khi order success, selected items tự động xóa
   - Non-selected items vẫn ở cart

4. **User Authentication**
   - Checkout có thể anonymous (phone + email)
   - OrderHistory yêu cầu login

5. **Shipping Cost**
   - Standard: 30.000 ₫
   - Express: 60.000 ₫
   - Tự động thêm vào total

---

## 📊 API Endpoints

| Method | Path | Request | Response | Auth |
|--------|------|---------|----------|------|
| POST | `/api/order/create` | CreateOrderRequest | {success, data: OrderResponse} | Optional |
| GET | `/user/cart` | - | Render Cart.html | Optional |
| GET | `/user/checkout` | - | Render Checkout.html | Optional |
| GET | `/user/order-history` | page, orderId | Render OrderHistory.html | Required |

---

## 🎨 UI/UX Details

### **Color Scheme:**
- Primary: Orange (#f97316)
- Success: Green (#22c55e)
- Error: Red (#ef4444)
- Warning: Yellow (#eab308)
- Neutral: Gray (#6b7280)

### **Selected Item UI:**
- Border: 2px solid orange-500
- Background: orange-50
- Text remains black for contrast

### **Status Colors:**
- PENDING: Yellow (⏳ Chờ xác nhận)
- CONFIRMED: Blue (✓ Đã xác nhận)
- SHIPPED: Purple (🚚 Đang giao)
- DELIVERED: Green (✅ Đã giao)
- CANCELLED: Red (❌ Đã hủy)

---

## 🔐 Security Considerations

1. **Validation**
   - Form fields validated server-side
   - Phone number format check (recommended)
   - Address not empty

2. **CSRF Protection**
   - Spring Security enabled
   - Thymeleaf forms support CSRF tokens

3. **User Isolation**
   - OrderHistory chỉ lấy user's own orders
   - CartItems xoá theo user

4. **Data Integrity**
   - Quantity > 0 validation
   - Price snapshot at purchase (không bị thay đổi giá)

---

## 📝 Database Schema

```
┌─────────────────────────────────────────┐
│              Users                      │
│ user_id, username, email, ...          │
└──────────────┬──────────────────────────┘
               │
         ┌─────┴──────┐
         │            │
    ┌────▼────┐  ┌────▼────────┐
    │ CartItems│ │   Orders     │
    │ (temp)   │  │ (permanent) │
    └──────────┘  └────┬────────┘
                        │
                   ┌────▼──────────┐
                   │ OrderDetails   │
                   │ (items detail) │
                   └────────────────┘
```

---

## ✅ Validation Checklist

- [ ] CartItem checkbox visible và functional
- [ ] Selected items highlight với viền cam + nền cam
- [ ] Số lượng thay đổi không gọi API
- [ ] Tóm tắt chỉ hiển thị selected items
- [ ] Nút Thanh Toán disable khi không chọn
- [ ] Checkout page load selected items từ localStorage
- [ ] Form validation hiển thị error message
- [ ] Submission disabled khi gửi request
- [ ] Success toast hiển thị sau khi đặt hàng
- [ ] Redirect đến order-history với orderId
- [ ] Order hiển thị đúng trong list
- [ ] OrderDetails hiển thị sản phẩm đúng
- [ ] Status badge hiển thị đúng màu
- [ ] Email layout responsive trên mobile

---

**🎉 Hoàn Thành Toàn Bộ Tính Năng Tách Giỏ Hàng & Thanh Toán!**
