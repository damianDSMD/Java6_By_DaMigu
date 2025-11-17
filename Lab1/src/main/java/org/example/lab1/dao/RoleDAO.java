package org.example.lab1.dao;

import org.example.lab1.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleDAO extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);
    boolean existsById(String id);
}
