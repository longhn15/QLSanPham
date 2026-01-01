package com.example.QLSanPham.controller.view.admin;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.QLSanPham.entity.Order;
import com.example.QLSanPham.entity.OrderStatus;
import com.example.QLSanPham.service.impl.OrderService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             @RequestParam(value = "status", required = false) String status,
                             @RequestParam(value = "type", required = false) String type,
                             @RequestParam(value = "q", required = false) String keyword,
                             Model model) {

        OrderStatus statusFilter = null;
        if (status != null && !status.isBlank()) {
            try {
                statusFilter = OrderStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid status filter
            }
        }

        Boolean isFlashSale = null;
        if ("flash".equalsIgnoreCase(type)) {
            isFlashSale = Boolean.TRUE;
        } else if ("normal".equalsIgnoreCase(type)) {
            isFlashSale = Boolean.FALSE;
        }

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orders = orderService.searchOrdersForAdmin(statusFilter, isFlashSale, keyword, pageable);

        Set<Long> flashOrderIds = new HashSet<>();
        orders.forEach(order -> {
            boolean hasFlash = order.getOrderDetails() != null && order.getOrderDetails().stream()
                    .anyMatch(d -> d.getFlashSaleItem() != null);
            if (hasFlash) {
                flashOrderIds.add(order.getId());
            }
        });

        model.addAttribute("orders", orders);
        model.addAttribute("statuses", EnumSet.allOf(OrderStatus.class));
        model.addAttribute("statusFilter", statusFilter);
        model.addAttribute("typeFilter", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("flashOrderIds", flashOrderIds);
        model.addAttribute("pendingOrderCount", orderService.countPendingOrders());
        return "admin/OrderAdmin";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getOrderWithDetails(id);
            model.addAttribute("order", order);
            model.addAttribute("pendingOrderCount", orderService.countPendingOrders());
            return "admin/OrderAdminDetail";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateStatusAndRedirect(id, OrderStatus.CONFIRMED, redirectAttributes);
    }

    @PostMapping("/{id}/ship")
    public String ship(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateStatusAndRedirect(id, OrderStatus.SHIPPED, redirectAttributes);
    }

    @PostMapping("/{id}/deliver")
    public String deliver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateStatusAndRedirect(id, OrderStatus.DELIVERED, redirectAttributes);
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        return updateStatusAndRedirect(id, OrderStatus.CANCELLED, redirectAttributes);
    }

    private String updateStatusAndRedirect(Long id, OrderStatus target, RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(id, target);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/orders";
    }
}
