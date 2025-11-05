package org.example.lab1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * LoginController - Handles custom login page and login/logout redirects
 * Required for Bài 3
 */
@Controller
@RequestMapping("/login")
public class LoginController {

    /**
     * Display login form
     * Accessed when user tries to access protected resource or navigates to /login/form
     */
    @GetMapping("/form")
    public String loginForm(Model model) {
        model.addAttribute("message", "Vui lòng đăng nhập");
        return "login-form";
    }

    /**
     * Login success page
     * Redirected here after successful authentication
     */
    @RequestMapping("/success")
    public String loginSuccess(Model model) {
        model.addAttribute("message", "Đăng nhập thành công");
        return "page";
    }

    /**
     * If Login fail => redirect back to home
     */
    @GetMapping("/failure")
    public String loginFailure(Model model) {
        model.addAttribute("message", "Đăng nhập thất bại");
        return "login-form";
    }

    /**
     * Logout => redirect back to login form
     */
    @GetMapping("/exit")
    public String logout(Model model) {
        model.addAttribute("message", "Đăng xuất thành công");
        return "login-form";
    }
}