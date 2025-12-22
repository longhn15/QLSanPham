package com.example.QLSanPham.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data; // Dữ liệu kèm theo (nếu có)

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}