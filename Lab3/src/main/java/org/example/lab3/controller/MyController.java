package org.example.lab3.controller;

import org.example.lab3.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyController {

    private final AuthService auth;

    public MyController(AuthService auth) {
        this.auth = auth;
    }

    @GetMapping({"/", "/poly/url0"})
    public String home(Model model) {
        model.addAttribute("message", "Trang chủ hoặc /poly/url0");
        return "page";
    }

    @GetMapping("/poly/url1")
    @PreAuthorize("isAuthenticated()")
    public String url1(Model model) {
        model.addAttribute("message", "/poly/url1 → Yêu cầu đăng nhập");
        return "page";
    }

    @GetMapping("/poly/url2")
    @PreAuthorize("hasRole('USER')")
    public String url2(Model model) {
        model.addAttribute("message", "/poly/url2 → Chỉ USER");
        return "page";
    }

    @GetMapping("/poly/url3")
    @PreAuthorize("hasRole('ADMIN')")
    public String url3(Model model) {
        model.addAttribute("message", "/poly/url3 → Chỉ ADMIN");
        return "page";
    }

    @GetMapping("/poly/url4")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String url4(Model model) {
        model.addAttribute("message", "/poly/url4 → USER hoặc ADMIN");
        return "page";
    }
}