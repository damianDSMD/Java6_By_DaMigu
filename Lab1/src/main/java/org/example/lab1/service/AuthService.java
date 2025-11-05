package org.example.lab1.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * AuthService - Helper service for working with authentication
 * Required for Bài 2
 */
@Service
public class AuthService {

    /**
     * Get the current authentication object from SecurityContext
     * @return Current Authentication or null if not authenticated
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Check if current user is authenticated (not anonymous)
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        Authentication auth = getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser");
    }

    /**
     * Get the username of the currently logged-in user
     * @return Username or "Anonymous" if not authenticated
     */
    public String getUsername() {
        if (isAuthenticated()) {
            return getAuthentication().getName();
        }
        return "Anonymous";
    }

    /**
     * Check if current user has a specific role
     * @param role Role name (without ROLE_ prefix)
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        if (!isAuthenticated()) {
            return false;
        }
        return getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }

    /**
     * Get all authorities/roles of current user as a comma-separated string
     * @return String of authorities or "None" if not authenticated
     */
    public String getAuthorities() {
        if (!isAuthenticated()) {
            return "None";
        }
        return getAuthentication().getAuthorities().toString();
    }
}