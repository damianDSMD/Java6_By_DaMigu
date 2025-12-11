package org.example.lab1.service;

import org.example.lab1.entity.Role;
import org.example.lab1.entity.User;
import org.example.lab1.entity.UserRole;
import org.example.lab1.entity.RegisterRequest;
import org.example.lab1.repository.RoleRepository;
import org.example.lab1.repository.UserRepository;
import org.example.lab1.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private UserRoleRepository userRoleRepo;

    @Autowired
    private PasswordEncoder encoder;

    public User register(RegisterRequest req) {

        if (userRepo.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists!");
        }

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Create user
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEmail(req.getEmail());
        user.setFullname(req.getFullname());
        user.setActivated(true);
        user.setAdmin(false);
        user.setProvider("local");
        user.setProviderId(null);

        user = userRepo.save(user);

        // Assign USER role
        Role role = roleRepo.findById("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER missing!"));

        UserRole ur = new UserRole();
        ur.setRole(role);
        ur.setUser(user);
        userRoleRepo.save(ur);

        return user;
    }
}
