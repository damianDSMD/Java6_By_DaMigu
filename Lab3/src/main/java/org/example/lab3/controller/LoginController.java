package org.example.lab3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login/form")
    public String form(@RequestParam(required = false) String error,
                       @RequestParam(required = false) String logout,
                       Model model) {
        if (error != null) {
            model.addAttribute("message", "Sai tài khoản hoặc mật khẩu!");
        } else if (logout != null) {
            model.addAttribute("message", "Đã đăng xuất thành công!");
        } else {
            model.addAttribute("message", "Vui lòng đăng nhập");
        }
        return "login"; // file login.html
    }

    @GetMapping({"/login/success", "/login/exit"})
    public String home(Model model) {
        model.addAttribute("message", "Chào mừng đến với hệ thống!");
        return "page"; // về trang page, không phải login nữa
    }

    @GetMapping("/login/failure")
    public String failure(Model model) {
        return "redirect:/login/form?error";
    }
}