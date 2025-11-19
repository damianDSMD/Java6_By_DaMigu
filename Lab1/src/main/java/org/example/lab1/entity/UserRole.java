package org.example.lab1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "UserRoles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"Username", "RoleId"})
})
public class UserRole implements Serializable {

    @Id
    // MSSQL uses IDENTITY instead of SERIAL
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Username", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "RoleId", nullable = false)
    private Role role;

    @Column(name = "AssignedAt", updatable = false)
    private LocalDateTime assignedAt;

    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}