package com.idcard.controller;

import com.idcard.entity.User;
import com.idcard.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.getUserByEmail(principal.getUsername());
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/profile")
    public String profileForm(@AuthenticationPrincipal UserDetails principal, Model model) {
        User user = userService.getUserByEmail(principal.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails principal,
                                @ModelAttribute User incoming,
                                @RequestParam(value = "photo", required = false) MultipartFile photo,
                                RedirectAttributes flash) {
        try {
            User current = userService.getUserByEmail(principal.getUsername());
            userService.updateUserDetails(current.getId(), incoming, photo);
            flash.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Update failed: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}
