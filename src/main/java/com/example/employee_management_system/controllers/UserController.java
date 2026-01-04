package com.example.employee_management_system.controllers;

import com.example.employee_management_system.Models.ERole;
import com.example.employee_management_system.Models.Role;
import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.entity.User;
import com.example.employee_management_system.repository.EmployeeRepository;
import com.example.employee_management_system.repository.LeaveRequestRepository;
import com.example.employee_management_system.repository.RoleRepository;
import com.example.employee_management_system.service.DepartmentService;
import com.example.employee_management_system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository; // Make sure this is injected

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/all")
    public String listUsers(Model model) {
        List<User> listUsers = userService.getAllUsers();
        model.addAttribute("listUsers", listUsers);
        return "liste_users";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Create a new Employee object for the form
        Employee employee = new Employee();
        model.addAttribute("employee", employee);

        // Add departments list for selection
        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);

        return "new_user";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute("employee") Employee employee,
                           @RequestParam(required = false) String role,
                           @RequestParam(required = false) Long departmentId) {

        try {
            // Encode password
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));

            // Set role based on selection
            Set<Role> roles = new HashSet<>();
            if (role != null && !role.isEmpty()) {
                switch (role) {
                    case "ROLE_ADMIN":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Role not found: ADMIN"));
                        roles.add(adminRole);
                        break;
                    case "ROLE_HR":
                        Role hrRole = roleRepository.findByName(ERole.ROLE_HR)
                                .orElseThrow(() -> new RuntimeException("Role not found: HR"));
                        roles.add(hrRole);
                        break;
                    case "ROLE_MANAGER":
                        Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Role not found: MANAGER"));
                        roles.add(managerRole);
                        break;
                    default:
                        Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                .orElseThrow(() -> new RuntimeException("Role not found: EMPLOYEE"));
                        roles.add(employeeRole);
                }
            } else {
                // Default to employee role
                Role employeeRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                        .orElseThrow(() -> new RuntimeException("Role not found: EMPLOYEE"));
                roles.add(employeeRole);
            }
            employee.setRoles(roles);

            // Set default values if not provided
            if (employee.getPosition() == null || employee.getPosition().isEmpty()) {
                employee.setPosition("Employee");
            }
            if (employee.getSalary() == null) {
                employee.setSalary(0.0);
            }
            if (employee.getHireDate() == null) {
                employee.setHireDate(LocalDate.now());
            }

            // Set department if provided
            if (departmentId != null) {
                try {
                    Department department = departmentService.getDepartmentById(departmentId);
                    employee.setDepartment(department);
                } catch (Exception e) {
                    System.out.println("Warning: Department not found with ID: " + departmentId);
                }
            }

            // Save Employee (which will also save User due to inheritance)
            employeeRepository.save(employee);

            return "redirect:/admin/employees?success=User created successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/employees?error=Error creating user: " + e.getMessage();
        }
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        try {
            // Try to get Employee first
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
            model.addAttribute("employee", employee);
            return "update_user";
        } catch (Exception e) {
            // Fallback to User if Employee not found
            User user = userService.getUserById(id);
            if (user != null) {
                model.addAttribute("user", user);
                return "update_user";
            } else {
                return "redirect:/admin/employees?error=User not found";
            }
        }
    }

    @PostMapping("/update/{id}")
    public String updateUser(@PathVariable("id") Long id,
                             @ModelAttribute("employee") Employee employee,
                             @RequestParam(required = false) String password,
                             @RequestParam(required = false) Long departmentId) {

        try {
            // Get existing employee
            Employee existingEmployee = employeeRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

            // Update basic info
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setPhone(employee.getPhone());
            existingEmployee.setPosition(employee.getPosition());
            existingEmployee.setSalary(employee.getSalary());
            existingEmployee.setHireDate(employee.getHireDate());

            // Update password if provided
            if (password != null && !password.trim().isEmpty()) {
                existingEmployee.setPassword(passwordEncoder.encode(password));
            }

            // Update department if provided
            if (departmentId != null) {
                try {
                    Department department = departmentService.getDepartmentById(departmentId);
                    existingEmployee.setDepartment(department);
                } catch (Exception e) {
                    System.out.println("Warning: Department not found with ID: " + departmentId);
                }
            }

            employeeRepository.save(existingEmployee);
            return "redirect:/admin/employees?success=User updated successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/admin/employees/" + id + "?error=Error updating user: " + e.getMessage();
        }
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteUser(@PathVariable("id") Long id) {
        try {
            // First try to delete Employee (which will cascade to User due to inheritance)
            if (employeeRepository.existsById(id)) {
                // Get the employee first to check if it exists
                Employee employee = employeeRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

                // Delete all leave requests for this employee first
                List<com.example.employee_management_system.entity.LeaveRequest> leaveRequests =
                        leaveRequestRepository.findByEmployeeId(id);

                if (!leaveRequests.isEmpty()) {
                    leaveRequestRepository.deleteAll(leaveRequests);
                }

                // Now delete the employee
                employeeRepository.deleteById(id);
            } else {
                // If no Employee record, delete User
                userService.deleteUser(id);
            }
            return "redirect:/admin/employees?success=User deleted successfully";
        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = "Error deleting user: " + e.getMessage();
            return "redirect:/admin/employees?error=" + encodeUrlParameter(errorMessage);
        }
    }

    // Helper method to encode URL parameters
    private String encodeUrlParameter(String param) {
        return param.replace(" ", "%20")
                .replace("[", "%5B")
                .replace("]", "%5D")
                .replace("(", "%28")
                .replace(")", "%29")
                .replace("{", "%7B")
                .replace("}", "%7D");
    }
}