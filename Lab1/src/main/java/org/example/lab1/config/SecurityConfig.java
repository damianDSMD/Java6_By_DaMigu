package org.example.lab1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder pe) {
        String password = pe.encode("123");

        UserDetails user1 = User.withUsername("user@gmail.com")
                .password(password)
                .roles()
                .build();

        UserDetails user2 = User.withUsername("admin@gmail.com")
                .password(password)
                .roles()
                .build();

        UserDetails user3 = User.withUsername("both@gmail.com")
                .password(password)
                .roles()
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Bỏ cấu hình mặc định CSRF và CORS
        http.csrf(config -> config.disable())
                .cors(config -> config.disable());

        // Phân quyền sử dụng - BÀI 3
        http.authorizeHttpRequests(config -> {
            // Cho phép truy cập các trang login
            config.requestMatchers("/login/**").permitAll();
            // Yêu cầu đăng nhập cho /poly/**
            config.requestMatchers("/poly/**").authenticated();
            // Cho phép truy cập nặc danh cho tất cả các URL khác
            config.anyRequest().permitAll();
        });

        // Form đăng nhập tùy biến - BÀI 3
        http.formLogin(config -> {
            config.loginPage("/login/form")              // Trang form đăng nhập
                    .loginProcessingUrl("/login")          // URL xử lý đăng nhập
                    .defaultSuccessUrl("/login/success", true)   // Đăng nhập thành công
                    .failureUrl("/login/failure")          // Đăng nhập thất bại
                    .permitAll();
        });

        // Ghi nhớ tài khoản
        http.rememberMe(config -> {
            config.tokenValiditySeconds(3 * 24 * 60 * 60)  // 3 days
                    .rememberMeParameter("remember-me");
        });

        // Đăng xuất tùy biến - BÀI 3
        http.logout(config -> {
            config.logoutUrl("/logout")
                    .logoutSuccessUrl("/login/exit")       // Đăng xuất thành công
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll();
        });

        return http.build();
    }
}