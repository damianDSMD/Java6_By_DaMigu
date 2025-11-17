package org.example.lab1.dao;

import org.example.lab1.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleDAO extends JpaRepository<UserRole, String> {
    @Query("SELECT ur FROM UserRole ur WHERE ur.user.username = ?1")
    List<UserRole> findByUsername(String username);

    @Query("SELECT ur FROM UserRole ur WHERE ur.role.id = ?1")
    List<UserRole> findByRoleId(String roleId);
    void deleteByUser_Username(String username);
}

