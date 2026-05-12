package com.SneakySolo.SecureWatch.Controller;

import com.SneakySolo.SecureWatch.Dto.AuthResponseDTO;
import com.SneakySolo.SecureWatch.Dto.BlockRequestDTO;
import com.SneakySolo.SecureWatch.Dto.LoginRequestDTO;
import com.SneakySolo.SecureWatch.Service.AdminService;
import com.SneakySolo.SecureWatch.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/dashboard/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/dashboard/login")
    public String loginSubmit(@RequestParam String username,
                              @RequestParam String password,
                              HttpServletRequest request,
                              HttpSession session,
                              Model model) {
        try {
            LoginRequestDTO dto = new LoginRequestDTO();
            dto.setUsername(username);
            dto.setPassword(password);

            AuthResponseDTO response = userService.login(dto, request.getRemoteAddr());

            if (!response.getRole().equals("ADMIN")) {
                model.addAttribute("error", "Access denied. Admin credentials required.");
                return "login";
            }

            session.setAttribute("adminUser", response.getUsername());
            return "redirect:/dashboard/users";

        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials. " + e.getMessage());
            return "login";
        }
    }

    @GetMapping("/dashboard/users")
    public String users(HttpSession session, Model model) {
        if (session.getAttribute("adminUser") == null) return "redirect:/dashboard/login";
        model.addAttribute("users", adminService.getAllActiveUsers());
        model.addAttribute("adminUser", session.getAttribute("adminUser"));
        return "users";
    }

    @GetMapping("/dashboard/users/{username}/events")
    public String userEvents(@PathVariable String username,
                             HttpSession session,
                             Model model) {
        if (session.getAttribute("adminUser") == null) return "redirect:/dashboard/login";
        model.addAttribute("events", adminService.getUserSuspiciousEvents(username));
        model.addAttribute("targetUser", username);
        model.addAttribute("adminUser", session.getAttribute("adminUser"));
        return "user-events";
    }

    @PostMapping("/dashboard/users/{username}/block")
    public String blockUser(@PathVariable String username, HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/dashboard/login";
        String adminUsername = (String) session.getAttribute("adminUser");

        BlockRequestDTO dto = new BlockRequestDTO();
        dto.setEntityType("USER");
        dto.setEntityValue(username);
        dto.setReason("Manually blocked by admin");
        adminService.blockEntity(dto, adminUsername);

        return "redirect:/dashboard/users";
    }

    @GetMapping("/dashboard/blocked")
    public String blocked(HttpSession session, Model model) {
        if (session.getAttribute("adminUser") == null) return "redirect:/dashboard/login";
        model.addAttribute("blockedList", adminService.getAllBlockedEntities());
        model.addAttribute("adminUser", session.getAttribute("adminUser"));
        return "blocked";
    }

    @PostMapping("/dashboard/unblock/{id}")
    public String unblock(@PathVariable Integer id, HttpSession session) {
        if (session.getAttribute("adminUser") == null) return "redirect:/dashboard/login";
        adminService.unblockEntity(id);
        return "redirect:/dashboard/blocked";
    }

    @GetMapping("/dashboard/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/dashboard/login";
    }
}