package com.example.employee_management_system.service;

import com.example.employee_management_system.entity.Department;
import java.util.List;

public interface DepartmentService {
    Department createDepartment(Department department);
    List<Department> getAllDepartments();
    Department getDepartmentById(Long id);
    Department updateDepartment(Department department);
    void deleteDepartment(Long id);
    Department findByName(String name);

    long getTotalDepartments();
    long countEmployeesInDepartment(Long departmentId);
}