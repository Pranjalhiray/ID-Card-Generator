package com.idcard.controller;

import com.idcard.service.AdminService;
import com.idcard.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService  userService;
    private final AdminService adminService;

    public AdminController(UserService userService, AdminService adminService) {
        this.userService  = userService;
        this.adminService = adminService;
    }

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("pendingUsers",  userService.getPendingUsers());
        model.addAttribute("allUsers",      userService.getAllUsers());
        model.addAttribute("totalUsers",    userService.countTotal());
        model.addAttribute("pendingCount",  userService.countPending());
        model.addAttribute("approvedCount", userService.countApproved());
        return "admin-panel";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id, RedirectAttributes flash) {
        try {
            adminService.approveUser(id);
            flash.addFlashAttribute("success", "User approved and ID card generated.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Approval failed: " + e.getMessage());
        }
        return "redirect:/admin/panel";
    }

    @PostMapping("/reject/{id}")
    public String reject(@PathVariable Long id, RedirectAttributes flash) {
        adminService.rejectUser(id);
        flash.addFlashAttribute("warning", "User rejected.");
        return "redirect:/admin/panel";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes flash) {
        adminService.deleteUser(id);
        flash.addFlashAttribute("warning", "User deleted.");
        return "redirect:/admin/panel";
    }
}
