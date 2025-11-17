package org.example.lab1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * User Entity - Enhanced with OAuth2 support
 * Supports both local authentication and OAuth2 providers (Google, Facebook, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id
    @Column(name = "\"Username\"")
    private String username;

    @Column(name = "\"Password\"")
    private String password;

    @Column(name = "\"Fullname\"")
    private String fullname;

    @Column(name = "\"Email\"")
    private String email;

    @Column(name = "\"Photo\"")
    private String photo;

    @Column(name = "\"Activated\"")
    private Boolean activated = true;

    @Column(name = "\"Admin\"")
    private Boolean admin = false;

    @Column(name = "\"Provider\"")
    private String provider;

    @Column(name = "\"ProviderId\"")
    private String providerId;

    @Column(name = "\"CreatedAt\"")
    private LocalDateTime createdAt;

    @Column(name = "\"UpdatedAt\"")
    private LocalDateTime updatedAt;

    // Relationships remain the same
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserRole> userRoles;

    // JPA Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if user is using OAuth2 authentication
     */
    public boolean isOAuth2User() {
        return provider != null && !provider.equals("local");
    }

    /**
     * Check if user needs password (local authentication)
     */
    public boolean needsPassword() {
        return "local".equals(provider) || provider == null;
    }
}