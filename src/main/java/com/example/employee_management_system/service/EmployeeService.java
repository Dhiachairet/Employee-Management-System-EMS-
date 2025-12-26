package com.example.employee_management_system.service;

import com.example.employee_management_system.entity.Employee;

import java.util.List;

public interface EmployeeService {
    Employee createEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);  // Use this instead!
    Employee updateEmployee(Employee employee);
    void deleteEmployee(Long id);
    // findByEmployeeId REMOVED
    List<Employee> getEmployeesByDepartment(Long departmentId);
    List<Employee> getEmployeesByRole(String role);
}