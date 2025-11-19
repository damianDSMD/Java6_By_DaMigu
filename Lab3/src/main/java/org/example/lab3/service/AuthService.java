package org.example.lab3.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service("auth")
public class AuthService {

    public String getUsername() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null ? a.getName() : null;
    }

    public List<String> getRoles() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a == null) return List.of();
        return a.getAuthorities().stream()
                .map(granted -> granted.getAuthority().replace("ROLE_", ""))
                .toList();
    }

    public boolean isAuthenticated() {
        String name = getUsername();
        return name != null && !"anonymousUser".equals(name);
    }

    public boolean hasAnyRole(String... roles) {
        return getRoles().stream().anyMatch(r -> java.util.Arrays.asList(roles).contains(r));
    }
}