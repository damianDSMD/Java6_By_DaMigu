package org.example.lab1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Users")
public class User implements Serializable {

    @Id
    @Column(name = "Username", length = 50, nullable = false)
    private String username;

    @Column(name = "Password", length = 500)
    private String password;

    @Column(name = "Fullname", length = 100)
    private String fullname;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @Column(name = "Photo", length = 500)
    private String photo;

    @Column(name = "Activated", nullable = false)
    private Boolean activated = true;

    // MSSQL uses "Admin" as column name (reserved word, use brackets in SQL)
    @Column(name = "Admin", nullable = false)
    private Boolean admin = false;

    @Column(name = "Provider", length = 20)
    private String provider;

    @Column(name = "ProviderId", length = 100)
    private String providerId;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserRole> userRoles;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}