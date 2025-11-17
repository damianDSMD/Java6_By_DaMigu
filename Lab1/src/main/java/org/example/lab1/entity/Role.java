package org.example.lab1.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Role Entity
 * Represents a role in the system (USER, ADMIN, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "\"Roles\"")
public class Role implements Serializable {

    @Id
    @Column(name = "\"Id\"")
    private String id;

    @Column(name = "\"Name\"")
    private String name;

    @Column(name = "\"Description\"")
    private String description;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<UserRole> userRoles;
}