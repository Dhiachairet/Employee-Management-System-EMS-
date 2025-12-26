package com.example.employee_management_system.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    // Constructors
    public Department() {}

    public Department(String name, String description) {
        this.name = name;
        this.description = description;
    }
}