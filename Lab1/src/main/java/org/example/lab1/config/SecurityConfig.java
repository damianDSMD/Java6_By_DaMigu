package org.example.lab1.config;

import org.example.lab1.service.CustomOAuth2UserService;
import org.example.lab1.service.DaoUserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private DaoUserDetailsManager daoUserDetailsManager;

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(daoUserDetailsManager);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(cs -> cs.disable())
                .cors(cors -> cors.disable());

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/", "/login/**", "/access-denied", "/oauth2/**", "/register/**", "/poly/login").permitAll();
            auth.requestMatchers("/poly/url1").authenticated();
            auth.requestMatchers("/poly/url2").hasRole("USER");
            auth.requestMatchers("/poly/url3").hasRole("ADMIN");
            auth.requestMatchers("/poly/url4").hasAnyRole("USER", "ADMIN");
            auth.anyRequest().permitAll();
        });

        http.authenticationProvider(authenticationProvider());

        // add JWT filter BEFORE UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // existing form login, oauth2Login, remember-me, logout etc - keep them
        http.formLogin(form -> form
                .loginPage("/login/form")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login/failure")
                .permitAll()
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login/form")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login/failure")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        );

        // remember-me and logout config left as you had them...
        return http.build();
    }

    // Expose AuthenticationManager to be used by controllers/services
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
