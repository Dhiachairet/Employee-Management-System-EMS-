package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.Models.ERole;
import com.example.employee_management_system.Models.Role;
import com.example.employee_management_system.service.EmployeeService;
import com.example.employee_management_system.service.DepartmentService;
import com.example.employee_management_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public String listEmployees(Model model) {
        List<Employee> listEmployees = employeeService.getAllEmployees();
        model.addAttribute("listEmployees", listEmployees);
        return "liste_employees";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Employee employee = new Employee();
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("employee", employee);
        model.addAttribute("departments", departments);
        return "new_employee";
    }

    @PostMapping("/save")
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {
        // Set default role as EMPLOYEE
        Set<Role> roles = new HashSet<>();
        Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(employeeRole);
        employee.setRoles(roles);

        // Encode the password
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        employeeService.createEmployee(employee);
        return "redirect:/employees/all";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Employee employee = employeeService.getEmployeeById(id);
        List<Department> departments = departmentService.getAllDepartments();

        model.addAttribute("employee", employee);
        model.addAttribute("departments", departments);
        return "update_employee";
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable("id") Long id,
                                 @ModelAttribute("employee") Employee employee,
                                 @RequestParam(value = "newPassword", required = false) String newPassword,
                                 @RequestParam(value = "departmentId", required = false) Long departmentId) {

        Employee existingEmployee = employeeService.getEmployeeById(id);
        if (existingEmployee != null) {
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setPhone(employee.getPhone());
            existingEmployee.setPosition(employee.getPosition());
            existingEmployee.setSalary(employee.getSalary());
            existingEmployee.setHireDate(employee.getHireDate());

            if (newPassword != null && !newPassword.trim().isEmpty()) {
                existingEmployee.setPassword(passwordEncoder.encode(newPassword));
            }

            if (departmentId != null) {
                Department dept = departmentService.getDepartmentById(departmentId);
                existingEmployee.setDepartment(dept);
            } else {
                existingEmployee.setDepartment(null);
            }

            employeeService.updateEmployee(existingEmployee);
        }
        return "redirect:/employees/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/employees/all";
    }
}