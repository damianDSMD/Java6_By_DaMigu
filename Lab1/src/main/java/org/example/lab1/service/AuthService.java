package org.example.lab1.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    /**
     * Get the current authentication object
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");
    }

    /**
     * Get the username of the logged-in user
     */
    public String getUsername() {
        if (isAuthenticated()) {
            return getAuthentication().getName();
        }
        return "Anonymous";
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }
        return getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
}