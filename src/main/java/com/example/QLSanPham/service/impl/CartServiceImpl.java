package com.example.QLSanPham.service.impl;

import com.example.QLSanPham.dto.request.AddToCartRequest;
import com.example.QLSanPham.dto.request.UpdateCartItemRequest;
import com.example.QLSanPham.dto.response.CartItemResponse;
import com.example.QLSanPham.dto.response.CartResponse;
import com.example.QLSanPham.entity.CartItem;
import com.example.QLSanPham.entity.Product;
import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.exception.ResourceNotFoundException;
import com.example.QLSanPham.repository.CartItemRepository;
import com.example.QLSanPham.repository.ProductRepository;
import com.example.QLSanPham.repository.UserRepository;
import com.example.QLSanPham.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public CartItemResponse addToCart(AddToCartRequest request, Long userId, String sessionId) {
        // Tìm product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + request.getProductId()));

        // Kiểm tra tồn kho
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Sản phẩm không đủ số lượng trong kho");
        }

        CartItem cartItem;

        // Tìm cart item hiện có
        if (userId != null) {
            Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
            if (existingItem.isPresent()) {
                cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + request.getQuantity();
                
                if (product.getStockQuantity() < newQuantity) {
                    throw new IllegalStateException("Số lượng vượt quá tồn kho");
                }
                
                cartItem.setQuantity(newQuantity);
            } else {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
                
                cartItem = CartItem.builder()
                        .user(user)
                        .product(product)
                        .quantity(request.getQuantity())
                        .priceSnapshot(product.getPrice())
                        .build();
            }
        } else {
            Optional<CartItem> existingItem = cartItemRepository.findBySessionIdAndProductId(sessionId, request.getProductId());
            if (existingItem.isPresent()) {
                cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + request.getQuantity();
                
                if (product.getStockQuantity() < newQuantity) {
                    throw new IllegalStateException("Số lượng vượt quá tồn kho");
                }
                
                cartItem.setQuantity(newQuantity);
            } else {
                cartItem = CartItem.builder()
                        .sessionId(sessionId)
                        .product(product)
                        .quantity(request.getQuantity())
                        .priceSnapshot(product.getPrice())
                        .build();
            }
        }

        cartItem = cartItemRepository.save(cartItem);
        log.info("Added product {} to cart for user/session: {}/{}", product.getName(), userId, sessionId);

        return mapToCartItemResponse(cartItem);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId, String sessionId) {
        List<CartItem> cartItems;

        if (userId != null) {
            cartItems = cartItemRepository.findByUserId(userId);
        } else {
            cartItems = cartItemRepository.findBySessionId(sessionId);
        }

        List<CartItemResponse> items = cartItems.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        BigDecimal subtotal = cartItems.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Tính phí ship (giả định: miễn phí nếu > 500k, còn lại 30k)
        BigDecimal shippingFee = subtotal.compareTo(new BigDecimal("500000")) >= 0 
                ? BigDecimal.ZERO 
                : new BigDecimal("30000");

        BigDecimal total = subtotal.add(shippingFee);

        return CartResponse.builder()
                .items(items)
                .totalItems(cartItems.size())
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .total(total)
                .build();
    }

    @Override
    public CartItemResponse updateCartItem(UpdateCartItemRequest request, Long userId, String sessionId) {
        CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cart item với ID: " + request.getCartItemId()));

        // Kiểm tra quyền sở hữu
        if (userId != null && !cartItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền cập nhật item này");
        }
        if (userId == null && !cartItem.getSessionId().equals(sessionId)) {
            throw new IllegalStateException("Bạn không có quyền cập nhật item này");
        }

        // Kiểm tra tồn kho
        if (cartItem.getProduct().getStockQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Sản phẩm không đủ số lượng trong kho");
        }

        cartItem.setQuantity(request.getQuantity());
        cartItem = cartItemRepository.save(cartItem);

        log.info("Updated cart item {} to quantity {}", cartItem.getId(), request.getQuantity());

        return mapToCartItemResponse(cartItem);
    }

    @Override
    public void removeCartItem(Long cartItemId, Long userId, String sessionId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy cart item với ID: " + cartItemId));

        // Kiểm tra quyền sở hữu
        if (userId != null && !cartItem.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền xóa item này");
        }
        if (userId == null && !cartItem.getSessionId().equals(sessionId)) {
            throw new IllegalStateException("Bạn không có quyền xóa item này");
        }

        cartItemRepository.delete(cartItem);
        log.info("Removed cart item {}", cartItemId);
    }

    @Override
    public void clearCart(Long userId, String sessionId) {
        if (userId != null) {
            cartItemRepository.deleteByUserId(userId);
            log.info("Cleared cart for user {}", userId);
        } else {
            cartItemRepository.deleteBySessionId(sessionId);
            log.info("Cleared cart for session {}", sessionId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long userId, String sessionId) {
        if (userId != null) {
            return cartItemRepository.countByUserId(userId);
        } else {
            return cartItemRepository.countBySessionId(sessionId);
        }
    }

    @Override
    public void mergeSessionCartToUser(String sessionId, Long userId) {
        List<CartItem> sessionItems = cartItemRepository.findBySessionId(sessionId);
        
        if (sessionItems.isEmpty()) {
            return;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        for (CartItem sessionItem : sessionItems) {
            Optional<CartItem> existingUserItem = cartItemRepository
                    .findByUserIdAndProductId(userId, sessionItem.getProduct().getId());

            if (existingUserItem.isPresent()) {
                // Merge quantities
                CartItem userItem = existingUserItem.get();
                userItem.setQuantity(userItem.getQuantity() + sessionItem.getQuantity());
                cartItemRepository.save(userItem);
                cartItemRepository.delete(sessionItem);
            } else {
                // Transfer session item to user
                sessionItem.setUser(user);
                sessionItem.setSessionId(null);
                cartItemRepository.save(sessionItem);
            }
        }

        log.info("Merged session cart {} to user {}", sessionId, userId);
    }

    private CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(product.getImageUrl())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : "N/A")
                .price(cartItem.getPriceSnapshot())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
