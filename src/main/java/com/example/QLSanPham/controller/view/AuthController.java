package com.example.QLSanPham.controller.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.QLSanPham.dto.request.RegisterRequestDTO;
import com.example.QLSanPham.service.impl.UserService;

import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(required = false) String error,
            @RequestParam(required = false) String captchaError, Model model, HttpSession session) {
        if ("true".equals(error)) {
            Integer failCount = (Integer) session.getAttribute("loginFailCount");
            if (failCount != null && failCount >= 3) {
                model.addAttribute("showCaptcha", true);
            }
            model.addAttribute("loginError", "Sai tài khoản hoặc mật khẩu");
        }
        if ("true".equals(captchaError)) {
            model.addAttribute("showCaptcha", true);
            model.addAttribute("captchaError", "CAPTCHA không đúng");
        }
        return "auth/Login"; // login.html
    }

    // PostMapping đã được xử lý bởi Spring Security formLogin() 
    // với CustomAuthenticationFailureHandler và CustomAuthenticationSuccessHandler
    // KHÔNG CẦN PostMapping tại đây vì sẽ gây xung đột!

    public String generateCaptchaText() {
        int length = 5; // hoặc 6
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @GetMapping("/captcha")
    public void captcha(HttpSession session, HttpServletResponse response) throws IOException {
        String captchaText = generateCaptchaText();
        session.setAttribute("captcha", captchaText);

        int width = 150, height = 50;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // nền
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);

        // chữ
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        g.drawString(captchaText, 20, 35);

        // nhiễu
        Random r = new Random();
        for (int i = 0; i < 10; i++) {
            g.drawLine(r.nextInt(width), r.nextInt(height),
                    r.nextInt(width), r.nextInt(height));
        }

        g.dispose();

        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterRequestDTO());
        return "auth/Register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid RegisterRequestDTO
                                dto,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "auth/Register";
        }
        
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Mật khẩu xác nhận không khớp!");
            return "auth/Register";
        }

        // 3. Check lỗi Database (Email đã tồn tại)
        if (userService.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "error.user", "Email này đã được sử dụng!");
            return "auth/Register";
        }

        userService.registerUser(dto);
        return "redirect:/login?success";
        }
}
