package org.example.lab7.controller;

import org.example.lab7.entity.User;
import org.example.lab7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
class UserController {

    @Autowired
    private UserRepository repository;

    @GetMapping
    public List<User> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getById(@PathVariable String username) {
        return repository.findById(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return repository.save(user);
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> update(@PathVariable String username, @RequestBody User user) {
        if (repository.existsById(username)) {
            user.setUsername(username);
            return ResponseEntity.ok(repository.save(user));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> delete(@PathVariable String username) {
        if (repository.existsById(username)) {
            repository.deleteById(username);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}