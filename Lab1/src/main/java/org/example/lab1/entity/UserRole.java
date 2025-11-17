package org.example.lab1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * UserRole Entity (Junction Table)
 * Maps to UserRoles table in database
 * Represents many-to-many relationship between Users and Roles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"UserRoles\"", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"\"Username\"", "\"RoleId\""})
})
public class UserRole implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"Id\"")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "\"Username\"")
    private User user;

    @ManyToOne
    @JoinColumn(name = "\"RoleId\"")
    private Role role;
}