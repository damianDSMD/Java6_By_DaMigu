package org.example.lab3.config;

import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // BÀI 2 LAB3
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource); // DÙNG MSSQL THAY INMEMORY
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
                .cors(c -> c.disable())
                .authorizeHttpRequests(a -> a.anyRequest().permitAll())
                .formLogin(f -> f
                        .loginPage("/login/form")
                        .loginProcessingUrl("/login/check")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/", true)           // Đăng nhập thành công → về trang chủ
                        .failureUrl("/login/form?error")
                        .permitAll()
                )
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login/form?logout") // Đăng xuất → về form login + thông báo
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .oauth2Login(o -> o
                        .loginPage("/login/form")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                );

        return http.build();
    }

}