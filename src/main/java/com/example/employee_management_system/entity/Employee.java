package com.example.employee_management_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Employee extends User {

    private Double salary;
    private LocalDate hireDate;
    private String position;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @OneToMany(mappedBy = "employee")
    private List<LeaveRequest> leaveRequests;

    @OneToMany(mappedBy = "employee")
    private List<Performance> performances;

    public Employee() {}


    public Employee(String email, String password, String phone,
                    Double salary, LocalDate hireDate, String position) {
        super(email, password, phone);
        this.salary = salary;
        this.hireDate = hireDate;
        this.position = position;
    }

    public long calculateTenure() {
        return java.time.temporal.ChronoUnit.MONTHS.between(hireDate, LocalDate.now());
    }
}