package com.example.QLSanPham.exception;

/**
 * Exception khi cố gắng xóa sản phẩm đang có trong đơn hàng
 * 
 * @author Senior Developer
 */
public class ProductInUseException extends RuntimeException {
    
    public ProductInUseException(String message) {
        super(message);
    }
    
    public ProductInUseException(String message, Throwable cause) {
        super(message, cause);
    }
}
