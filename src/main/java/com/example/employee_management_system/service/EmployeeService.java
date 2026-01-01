package com.example.employee_management_system.service;

import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.entity.User;
import java.util.List;

public interface EmployeeService {

    Employee createEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee updateEmployee(Employee employee);
    void deleteEmployee(Long id);
    List<Employee> getEmployeesByDepartment(Long departmentId);
    List<Employee> getEmployeesByRole(String role);
    Employee getEmployeeByEmail(String email);
    long getTotalEmployees();
    long countEmployeesByDepartment(Long departmentId);

    // Add this method
    User getUserByEmail(String email);
}