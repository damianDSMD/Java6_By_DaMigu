package org.example.lab1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MyController {

    /**
     * Home page - mapped to both "/" and "/poly/url0"
     */
    @GetMapping({"/", "/poly/url0"})
    public String home(Model model, @RequestParam(required = false) String login) {
        if ("success".equals(login)) {
            model.addAttribute("message", "✅ Đăng nhập thành công! Welcome to home page");
        } else {
            model.addAttribute("message", "@/ or @/poly/url0 => home()");
        }
        return "page";
    }

    /**
     * URL 1 - Protected by authentication in Bài 2 & 3
     */
    @GetMapping("/poly/url1")
    public String method1(Model model) {
        model.addAttribute("message", "@/poly/url1 => method1()");
        return "page";
    }

    /**
     * URL 2 - Protected by authentication in Bài 2 & 3
     */
    @GetMapping("/poly/url2")
    public String method2(Model model) {
        model.addAttribute("message", "@/poly/url2 => method2()");
        return "page";
    }

    /**
     * URL 3 - Protected by authentication in Bài 2 & 3
     */
    @GetMapping("/poly/url3")
    public String method3(Model model) {
        model.addAttribute("message", "@/poly/url3 => method3()");
        return "page";
    }

    /**
     * URL 4 - Protected by authentication in Bài 2 & 3
     */
    @GetMapping("/poly/url4")
    public String method4(Model model) {
        model.addAttribute("message", "@/poly/url4 => method4()");
        return "page";
    }
}