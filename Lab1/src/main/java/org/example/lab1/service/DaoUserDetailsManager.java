package org.example.lab1.service;

import org.example.lab1.dao.UserDAO;
import org.example.lab1.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.stream.Collectors;


@Service
@Transactional
public class DaoUserDetailsManager implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user in database with roles eagerly loaded
        User user = userDAO.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username
                ));

        // Check if user is activated
        if (!user.getActivated()) {
            throw new UsernameNotFoundException(
                    "User account is deactivated: " + username
            );
        }

        // Convert user roles to Spring Security authorities
        Collection<GrantedAuthority> authorities = getAuthorities(user);

        // Build and return UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getActivated())
                .build();
    }

    /**
     * Convert User entity roles to Spring Security authorities
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(userRole -> {
                    // Role ID should be like "USER" or "ADMIN"
                    // Add "ROLE_" prefix if not present
                    String roleId = userRole.getRole().getId();
                    if (!roleId.startsWith("ROLE_")) {
                        roleId = "ROLE_" + roleId;
                    }
                    return new SimpleGrantedAuthority(roleId);
                })
                .collect(Collectors.toList());
    }

    /**
     * Get user by username (utility method)
     */
    public User getUserByUsername(String username) {
        return userDAO.findByUsernameWithRoles(username).orElse(null);
    }

    /**
     * Check if user exists
     */
    public boolean userExists(String username) {
        return userDAO.existsByUsername(username);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email).orElse(null);
    }
}