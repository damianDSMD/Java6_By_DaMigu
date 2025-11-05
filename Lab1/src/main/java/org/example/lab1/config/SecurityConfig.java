package org.example.lab1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Password Encoder Bean
     * Uses DelegatingPasswordEncoder (supports multiple encoding formats)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * UserDetailsService Bean
     * Manages user data in memory with 3 test users
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        String password = passwordEncoder.encode("123");

        UserDetails user1 = User.withUsername("user@gmail.com")
                .password(password)
                .roles("USER")
                .build();

        UserDetails user2 = User.withUsername("admin@gmail.com")
                .password(password)
                .roles("ADMIN")
                .build();

        UserDetails user3 = User.withUsername("both@gmail.com")
                .password(password)
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

    /**
     * SecurityFilterChain Bean
     * Configures security rules, authentication, and authorization
     *
     * CHANGE THIS BASED ON WHICH LAB YOU'RE DOING:
     * - Bài 1: Use permitAll() for all requests
     * - Bài 2: Use authenticated() for /poly/**
     * - Bài 3: Add custom login form configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF and CORS
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());

        // Authorization Configuration
        http.authorizeHttpRequests(auth -> {
            // Allow login pages without authentication
            auth.requestMatchers("/login/**").permitAll();

            // Require authentication for /poly/** paths
            auth.requestMatchers("/poly/**").authenticated();

            // Allow all other requests
            auth.anyRequest().permitAll();
        });

        // Custom Login Form Configuration (Bài 3)
        http.formLogin(form -> {
            form.loginPage("/login/form")                    // Custom login page
                    .loginProcessingUrl("/login")                // Where to submit login form
                    .defaultSuccessUrl("/login/success", true)   // Redirect after successful login
                    .failureUrl("/login/failure")                // Redirect after failed login
                    .permitAll();
        });

        // Remember Me Configuration
        http.rememberMe(remember -> {
            remember.tokenValiditySeconds(3 * 24 * 60 * 60)  // 3 days
                    .rememberMeParameter("remember-me");
        });

        // Logout Configuration
        http.logout(logout -> {
            logout.logoutUrl("/logout")
                    .logoutSuccessUrl("/login/exit")           // Redirect after logout
                    .deleteCookies("JSESSIONID", "remember-me")
                    .permitAll();
        });

        return http.build();
    }

    /*
     * ============================================
     * CONFIGURATION GUIDE FOR DIFFERENT LABS
     * ============================================
     *
     * BÀI 1: Basic Security (permitAll)
     * ----------------------------------
     * Replace authorization section with:
     *
     * http.authorizeHttpRequests(auth -> {
     *     auth.anyRequest().permitAll();
     * });
     *
     * http.formLogin(form -> form.permitAll());
     *
     *
     * BÀI 2: Authentication Required
     * -------------------------------
     * Replace authorization section with:
     *
     * http.authorizeHttpRequests(auth -> {
     *     auth.requestMatchers("/poly/**").authenticated();
     *     auth.anyRequest().permitAll();
     * });
     *
     * http.formLogin(form -> form.permitAll());
     *
     *
     * BÀI 3: Custom Login Form
     * -------------------------
     * Use the current configuration above (already configured)
     *
     */
}