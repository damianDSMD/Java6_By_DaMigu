package org.example.lab1.controller;

import org.example.lab1.entity.RegisterRequest;
import org.example.lab1.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    RegisterService registerService;

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register-form";
    }

    @PostMapping("/submit")
    public String submit(@ModelAttribute RegisterRequest req, Model model) {
        try {
            registerService.register(req);
            model.addAttribute("message", "Tạo tài khoản thành công!");
            model.addAttribute("username", req.getUsername());
            return "register-success";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register-form";
        }
    }
}
