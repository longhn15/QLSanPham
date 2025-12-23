package com.example.QLSanPham.controller.view.admin;


// import com.example.QLSanPham.service.FlashSaleService;
// import com.example.QLSanPham.service.OrderService;
// import com.example.QLSanPham.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Bảo mật 2 lớp
public class AdminDashBoardController {

    // private final FlashSaleService flashSaleService;
    // private final OrderService orderService;
    
    // 1. Dashboard thống kê
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Gọi các hàm thống kê từ Repository/Service
        // model.addAttribute("revenue", orderService.getTodayRevenue());
        // model.addAttribute("totalOrders", orderService.countTodayOrders());
        return "/admin/DashBoard"; // admin/dashboard.html
    }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        // model.addAttribute("products", productService.getAllProducts());
        // return "/user/HomePage";
        return "/user/FakeHomePage";
    }

    // // 2. Quản lý Flash Sale
    // @GetMapping("/flash-sales")
    // public String manageFlashSales(Model model) {
    //     model.addAttribute("sessions", flashSaleService.getAllSessions());
    //     return "admin/flash-sale-list";
    // }

    // // 3. Kích hoạt Session (Đẩy hàng vào Redis) - Logic quan trọng
    // @PostMapping("/flash-sales/{sessionId}/activate")
    // public String activateSession(@PathVariable Long sessionId, RedirectAttributes redirectAttributes) {
    //     try {
    //         flashSaleService.activateFlashSaleSession(sessionId);
    //         redirectAttributes.addFlashAttribute("message", "Đã kích hoạt Flash Sale và đồng bộ Redis thành công!");
    //     } catch (Exception e) {
    //         redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
    //     }
    //     return "redirect:/admin/flash-sales";
    // }
}
