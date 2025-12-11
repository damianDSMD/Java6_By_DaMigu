    package org.example.lab1.controller;

    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;


    @Controller
    @RequestMapping("/login")
    public class LoginController {


        // về trang đăng nhập khi vào 1 số trang
        @GetMapping("/form")
        public String loginForm(Model model) {
            model.addAttribute("message", "Vui lòng đăng nhập");
            return "login-form";
        }


        @RequestMapping("/success")
        public String loginSuccess(Model model) {
            model.addAttribute("message", "Đăng nhập thành công");
            return "page";
        }


        @GetMapping("/failure")
        public String loginFailure(Model model) {
            model.addAttribute("message", "Đăng nhập thất bại");
            return "login-form";
        }


        @GetMapping("/exit")
        public String logout(Model model) {
            model.addAttribute("message", "Đăng xuất thành công");
            return "login-form";
        }
    }