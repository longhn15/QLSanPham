package com.example.QLSanPham.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.QLSanPham.dto.request.CreateOrderRequest;
import com.example.QLSanPham.dto.response.OrderResponse;
import com.example.QLSanPham.entity.Order;
import com.example.QLSanPham.entity.OrderDetail;
import com.example.QLSanPham.entity.OrderStatus;
import com.example.QLSanPham.entity.Product;
import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.CartItemRepository;
import com.example.QLSanPham.repository.OrderRepository;
import com.example.QLSanPham.repository.ProductRepository;
import com.example.QLSanPham.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long userId, String sessionId) {
        // Validate user if authenticated
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        }

        // Calculate total from cart items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (var item : request.getCartItems()) {
            BigDecimal itemTotal = BigDecimal.valueOf(item.getPrice() * item.getQuantity());
            totalAmount = totalAmount.add(itemTotal);
        }

        // Add shipping cost if needed
        if ("express".equals(request.getShippingMethod())) {
            totalAmount = totalAmount.add(BigDecimal.valueOf(60000));
        } else {
            totalAmount = totalAmount.add(BigDecimal.valueOf(30000));
        }

        // Create order
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .orderDate(LocalDateTime.now())
                .build();

        // Add order details from cart items
        for (var cartItem : request.getCartItems()) {
            Long prodId = cartItem.getProductId();
            if (prodId == null) {
                // Try resolve via cart item id (legacy "id")
                Long cid = cartItem.getCartItemId() != null ? cartItem.getCartItemId() : cartItem.getId();
                if (cid != null) {
                    java.util.Optional<com.example.QLSanPham.entity.CartItem> ciOpt = cartItemRepository.findById(cid);
                    if (ciOpt.isPresent() && ciOpt.get().getProduct() != null) {
                        prodId = ciOpt.get().getProduct().getId();
                    }
                }
            }
            if (prodId == null) {
                throw new IllegalArgumentException("Sản phẩm không tồn tại");
            }
            Product product = productRepository.findById(prodId)
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

            OrderDetail detail = OrderDetail.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(BigDecimal.valueOf(cartItem.getPrice()))
                    .build();

            order.addDetail(detail);
        }

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Remove selected items from cart
        for (var cartItem : request.getCartItems()) {
            try {
                Long cid = cartItem.getCartItemId() != null ? cartItem.getCartItemId() : cartItem.getId();
                if (cid != null) {
                    cartItemRepository.deleteById(cid);
                }
            } catch (Exception e) {
                // Log but don't fail if cart item removal fails
                System.err.println("Failed to remove cart item: " + cartItem.getCartItemId());
            }
        }

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Đơn hàng không tồn tại"));
        
        // Check if order belongs to user
        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Bạn không có quyền hủy đơn hàng này");
        }
        
        // Only allow cancellation if order is PENDING
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalArgumentException("Chỉ có thể hủy đơn hàng đang chờ xác nhận");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

}
