package org.example.lab5.service.impl;

import org.example.lab5.repository.StudentRepo;
import org.example.lab5.entity.Student;
import org.example.lab5.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepo repo;

    @Override
    public List<Student> findAll() {
        return repo.findAll();
    }

    @Override
    public Student findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Override
    public Student create(Student student) {
        return repo.save(student);
    }

    @Override
    public Student update(Student student) {
        return repo.save(student);
    }

    @Override
    public void deleteById(String id) {
        repo.deleteById(id);
    }
}
