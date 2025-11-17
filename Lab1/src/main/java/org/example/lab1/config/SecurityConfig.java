package org.example.lab1.config;

import org.example.lab1.service.CustomOAuth2UserService;
import org.example.lab1.service.DaoUserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private DaoUserDetailsManager daoUserDetailsManager;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF and CORS (for development)
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable());

        // Authorization rules
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/", "/login/**", "/access-denied", "/oauth2/**").permitAll();
            auth.requestMatchers("/poly/url1").authenticated();
            auth.requestMatchers("/poly/url2").hasRole("USER");
            auth.requestMatchers("/poly/url3").hasRole("ADMIN");
            auth.requestMatchers("/poly/url4").hasAnyRole("USER", "ADMIN");
            auth.anyRequest().permitAll();
        });

        // Traditional form login
        http.formLogin(form -> {
            form.loginPage("/login/form")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login/failure")
                    .permitAll();
        });

        // OAuth2 login (Google)
        http.oauth2Login(oauth2 -> {
            oauth2.loginPage("/login/form")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login/failure")
                    .userInfoEndpoint(userInfo ->
                            userInfo.userService(customOAuth2UserService)
                    );
        });

        // Access denied handler
        http.exceptionHandling(exception -> {
            exception.accessDeniedPage("/access-denied");
        });

        // Remember me
        http.rememberMe(remember -> {
            remember.tokenValiditySeconds(7 * 24 * 60 * 60)
                    .rememberMeParameter("remember-me")
                    .userDetailsService(daoUserDetailsManager);  // ✅ Use injected bean directly
        });

        // Logout
        http.logout(logout -> {
            logout.logoutUrl("/logout")
                    .logoutSuccessUrl("/login/exit")
                    .deleteCookies("JSESSIONID", "remember-me")
                    .invalidateHttpSession(true)
                    .clearAuthentication(true)
                    .permitAll();
        });

        return http.build();
    }
}