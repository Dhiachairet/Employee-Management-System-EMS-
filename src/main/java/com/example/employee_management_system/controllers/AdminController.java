package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.service.DepartmentService;
import com.example.employee_management_system.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        // Get statistics for admin dashboard
        long totalEmployees = employeeService.getTotalEmployees();
        long totalDepartments = departmentService.getTotalDepartments();

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalDepartments", totalDepartments);

        return "admin/dashboard";
    }

    @GetMapping("/employees")
    public String manageEmployees(Model model) {
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        return "admin/employees";
    }

    @GetMapping("/departments")
    public String manageDepartments(Model model) {
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);
        return "admin/departments";
    }

    @GetMapping("/reports")
    public String viewReports() {
        return "admin/reports";
    }
}