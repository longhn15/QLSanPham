package com.example.QLSanPham.common;

import com.example.QLSanPham.entity.User;
import com.example.QLSanPham.repository.UserRepository;
import com.example.QLSanPham.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HeaderAttributes {

    private final UserRepository userRepository;
    private final CartService cartService;

    @ModelAttribute
    public void addHeaderAttributes(Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;
        String username = null;
        String email = null;
        // String avatarUrl = null;

        if (auth != null && auth.isAuthenticated() && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
            username = auth.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userId = user.getId();
                email = user.getEmail();
            }
        }

        String sessionId = session.getId();
        Integer cartCount = cartService.getCartItemCount(userId, sessionId);

        model.addAttribute("username", username);
        model.addAttribute("email", email);
        // model.addAttribute("avatarUrl", avatarUrl);
        model.addAttribute("cartCount", cartCount);
    }
}
