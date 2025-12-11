package org.example.lab5.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity(name = "J6Students")
public class Student {
    @Id
    private String id;
    private String name;
    private boolean gender;
    private double mark;
}