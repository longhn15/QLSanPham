# Tính Năng Checkout - Hướng Dẫn Thực Hiện

## 📋 Tổng Quan Tính Năng

Bạn đã request implement tính năng:
1. ✅ Checked items trong giỏ hàng **vẫn giữ nguyên** khi quay lại từ checkout
2. ✅ UserCheckoutController **pre-fill thông tin khách hàng** từ dữ liệu User đã đăng nhập
3. ✅ Hiển thị **full product details** (ảnh, tên, loại, số lượng, giá) ở checkout page
4. ✅ Chỉ **lưu vào database** khi khách hàng click "Đặt hàng"

## 🔧 Các Thay Đổi Thực Hiện

### 1. **UserCheckoutController.java** - Xử Lý Backend
```java
// ✅ Thêm @Autowired cho dependencies
@Autowired
private UserRepository userRepository;

@Autowired
private CartService cartService;

@Autowired
private CartItemRepository cartItemRepository;

// ✅ Pre-fill user information vào model
@GetMapping
public String viewCheckout(Model model, @AuthenticationPrincipal UserDetails userDetails) {
    Long userId = getUserId(userDetails);
    
    if (userId != null && userId > 0) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("userId", userId);
            model.addAttribute("userFullName", user.getFullName());
            model.addAttribute("userPhone", user.getPhoneNumber());
            model.addAttribute("userEmail", user.getEmail());
        }
    }
    
    return "user/Checkout";
}
```

### 2. **CartApiController.java** - Endpoint Mới
```java
// ✅ Endpoint để lấy full product details
@PostMapping("/get-selected-items")
public ResponseEntity<?> getSelectedItems(
        @RequestBody Map<String, List<Long>> request,
        ...) {
    
    List<Long> selectedIds = request.get("selectedItemIds");
    List<CartItem> cartItems = cartItemRepository.findAllById(selectedIds);
    
    List<CartItemResponse> items = cartItems.stream()
        .map(this::mapCartItemToResponse)
        .collect(Collectors.toList());
    
    return ResponseEntity.ok(Map.of(
        "success", true,
        "data", items
    ));
}

// Helper method - map CartItem to Response DTO
private CartItemResponse mapCartItemToResponse(CartItem cartItem) {
    return CartItemResponse.builder()
            .id(cartItem.getId())
            .productId(cartItem.getProduct().getId())
            .productName(cartItem.getProduct().getName())
            .productImage(cartItem.getProduct().getImageUrl())
            .categoryName(cartItem.getProduct().getCategory().getName())
            .price(cartItem.getPriceSnapshot())
            .quantity(cartItem.getQuantity())
            .totalPrice(cartItem.getTotalPrice())
            .stockQuantity(cartItem.getProduct().getStockQuantity())
            .build();
}
```

### 3. **Cart.html** - JavaScript
```javascript
// ✅ Lưu selected items vào localStorage
function saveSelectedItems() {
    const selectedCheckboxes = Array.from(document.querySelectorAll('.item-checkbox:checked'));
    const selectedIds = selectedCheckboxes.map(cb => {
        return cb.id.replace('select-item-', '');
    });
    localStorage.setItem('selectedCartItemIds', JSON.stringify(selectedIds));
}

// ✅ Restore selected items khi page load
function restoreSelectedItems() {
    const selectedIds = JSON.parse(localStorage.getItem('selectedCartItemIds') || '[]');
    selectedIds.forEach(itemId => {
        const checkbox = document.getElementById(`select-item-${itemId}`);
        if (checkbox) {
            checkbox.checked = true;
            toggleItemSelection(itemId);
        }
    });
}

// ✅ Gọi restore khi page load
document.addEventListener('DOMContentLoaded', () => {
    restoreSelectedItems();
});

// ✅ Lưu selection khi toggle
function toggleItemSelection(cartItemId) {
    // ... existing code ...
    saveSelectedItems(); // ← Add this line
}

// ✅ Lưu both old and new IDs vào localStorage
function proceedToCheckout() {
    const selectedItems = Array.from(document.querySelectorAll('.item-checkbox:checked')).map(cb => {
        const itemId = cb.id.replace('select-item-', '');
        const itemElement = document.querySelector(`[data-item-id="${itemId}"]`);
        return {
            id: itemId,
            quantity: parseInt(itemElement.dataset.quantity || '1'),
            price: parseFloat(itemElement.dataset.price || '0')
        };
    });

    localStorage.setItem('selectedCartItems', JSON.stringify(selectedItems));
    localStorage.setItem('selectedCartItemIds', JSON.stringify(selectedItems.map(item => item.id)));
    
    window.location.href = '/user/checkout';
}
```

### 4. **Checkout.html** - Frontend
```html
<!-- ✅ Pre-fill user information -->
<input type="text" id="fullName" name="fullName" required
       th:value="${userFullName != null ? userFullName : ''}"
       placeholder="Nhập họ tên">
       
<input type="tel" id="phone" name="phone" required
       th:value="${userPhone != null ? userPhone : ''}"
       placeholder="0123 456 789">

<input type="email" id="email" name="email"
       th:value="${userEmail != null ? userEmail : ''}"
       placeholder="example@email.com">
```

```javascript
// ✅ Fetch product details từ API
async function fetchProductDetails() {
    try {
        const { selectedItems, selectedIds } = loadSelectedItems();
        
        if (selectedIds.length === 0) {
            return selectedItems;
        }

        const response = await fetch('/api/cart/get-selected-items', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                selectedItemIds: selectedIds.map(id => parseInt(id))
            })
        });

        const data = await response.json();
        
        if (data.success && data.data) {
            return data.data;
        } else {
            return selectedItems;
        }
    } catch (error) {
        console.error('Error:', error);
        const { selectedItems } = loadSelectedItems();
        return selectedItems;
    }
}

// ✅ Render order items với full product info (ảnh, tên, loại, giá, số lượng)
function renderOrderItems(items) {
    const container = document.getElementById('order-items');
    container.innerHTML = '';
    
    let subtotal = 0;
    items.forEach(item => {
        const itemTotal = item.quantity * (item.price || item.priceSnapshot || 0);
        subtotal += itemTotal;
        
        const itemEl = document.createElement('div');
        itemEl.className = 'flex gap-3 pb-3 border-b border-gray-100 last:border-b-0';
        
        const hasImage = item.productImage;
        const productName = item.productName || `Sản phẩm #${item.id}`;
        const categoryName = item.categoryName || '';
        
        itemEl.innerHTML = `
            <div class="flex-1">
                ${hasImage ? `<div class="mb-2"><img src="${item.productImage}" alt="${productName}" class="w-16 h-16 object-cover rounded border border-gray-200"></div>` : ''}
                <p class="font-semibold text-gray-900">${productName}</p>
                ${categoryName ? `<p class="text-xs text-gray-500 mb-1">Loại: ${categoryName}</p>` : ''}
                <p class="text-sm text-gray-600">Số lượng: ${item.quantity}</p>
            </div>
            <div class="text-right">
                <p class="font-bold text-gray-900">${formatCurrency(itemTotal)}</p>
                <p class="text-xs text-gray-500">${formatCurrency(item.price || item.priceSnapshot)} x${item.quantity}</p>
            </div>
        `;
        container.appendChild(itemEl);
    });

    updateSummary(subtotal);
}

// ✅ Initialize checkout page với async loading
async function initializeCheckoutPage() {
    const items = await fetchProductDetails();
    renderOrderItems(items);
}

document.addEventListener('DOMContentLoaded', () => {
    initializeCheckoutPage();
});

initializeCheckoutPage();
```

## 🔄 Flow Hoạt Động

```
1. User tại Cart page:
   ├─ Checkbox sản phẩm muốn mua
   ├─ Selected items được lưu vào localStorage (saveSelectedItems)
   └─ Click "Thanh Toán"

2. System:
   ├─ Lưu selectedCartItems vào localStorage
   ├─ Lưu selectedCartItemIds vào localStorage
   └─ Redirect tới /user/checkout

3. Checkout page load:
   ├─ UserCheckoutController fetch user info
   ├─ Checkout.html pre-fill form với user data
   ├─ JavaScript call fetchProductDetails()
   ├─ API /api/cart/get-selected-items trả về full product info
   ├─ renderOrderItems() hiển thị đầy đủ (ảnh, tên, loại, giá, qty)
   └─ User xem lại và click "Đặt đơn hàng"

4. User click "Quay lại giỏ hàng":
   ├─ Browser back tới Cart page
   ├─ JavaScript restoreSelectedItems() trigger
   ├─ Tất cả previously selected items vẫn checked
   └─ Summary tính toán lại tương ứng

5. User click "Đặt hàng":
   ├─ submitOrder() POST tới /api/order/create
   ├─ Backend tạo Order + OrderDetails trong database
   ├─ Clear localStorage (selectedCartItems, selectedCartItemIds)
   ├─ Redirect tới /user/order-history
   └─ ✅ Done - dữ liệu lưu vào database
```

## 📝 API Endpoints

### GET `/user/checkout`
**Response:**
```json
{
  "userId": 1,
  "userFullName": "Nguyễn Văn A",
  "userPhone": "0912345678",
  "userEmail": "a@example.com",
  "user": { ... }
}
```

### POST `/api/cart/get-selected-items`
**Request:**
```json
{
  "selectedItemIds": [1, 2, 3]
}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "productId": 10,
      "productName": "Product A",
      "productImage": "http://...",
      "categoryName": "Electronics",
      "price": 100000,
      "quantity": 2,
      "totalPrice": 200000,
      "stockQuantity": 50
    },
    ...
  ]
}
```

## 🧪 Testing Checklist

- [ ] User login thành công
- [ ] Checkbox items tại Cart page
- [ ] Click "Thanh Toán" → chuyển tới Checkout
- [ ] Kiểm tra user info (fullName, phone, email) được pre-fill
- [ ] Kiểm tra order items hiển thị: ảnh, tên, loại, giá, số lượng
- [ ] Click "Quay lại giỏ hàng" → items vẫn checked
- [ ] Uncheck một item → back to checkout → item không hiển thị
- [ ] Fill thông tin và click "Đặt hàng"
- [ ] Check database: Order + OrderDetails được tạo
- [ ] localStorage: selectedCartItems được clear
- [ ] Redirect tới /user/order-history

## 📌 Ghi Chú

1. **localStorage persistence**: Chỉ lưu **IDs của items**, không lưu toàn bộ product details (để tiết kiệm bộ nhớ và luôn get latest data từ DB)
2. **User pre-fill**: Chỉ apply nếu user đã login (userId > 0)
3. **Async loading**: Checkout page fetch product details async để không block page rendering
4. **Database save**: Chỉ lưu khi submit order, không tự động save quantity changes
5. **Back button**: Browser back button hoạt động nhờ localStorage persistence

