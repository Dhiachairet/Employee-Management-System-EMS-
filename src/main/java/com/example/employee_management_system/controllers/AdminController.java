package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.entity.LeaveRequest;
import com.example.employee_management_system.repository.DepartmentRepository;
import com.example.employee_management_system.repository.LeaveRequestRepository;
import com.example.employee_management_system.service.DepartmentService;
import com.example.employee_management_system.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

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
    public String viewReports(Model model) {
        // Get all leaves for analytics
        List<LeaveRequest> allLeaves = leaveRequestRepository.findAll();
        model.addAttribute("allLeaves", allLeaves);

        // Get departments for filter
        List<Department> departments = departmentRepository.findAll();
        model.addAttribute("departments", departments);

        // Calculate statistics
        long totalLeaves = allLeaves.size();
        long approvedLeaves = allLeaves.stream().filter(l -> "APPROVED".equals(l.getStatus())).count();
        long pendingLeaves = allLeaves.stream().filter(l -> "PENDING".equals(l.getStatus())).count();
        long rejectedLeaves = allLeaves.stream().filter(l -> "REJECTED".equals(l.getStatus())).count();

        // Count by leave type
        long sickLeaves = allLeaves.stream().filter(l -> "SICK".equals(l.getLeaveType())).count();
        long vacationLeaves = allLeaves.stream().filter(l -> "VACATION".equals(l.getLeaveType())).count();
        long personalLeaves = allLeaves.stream().filter(l -> "PERSONAL".equals(l.getLeaveType())).count();
        long otherLeaves = allLeaves.stream()
                .filter(l -> !"SICK".equals(l.getLeaveType()) &&
                        !"VACATION".equals(l.getLeaveType()) &&
                        !"PERSONAL".equals(l.getLeaveType()))
                .count();

        model.addAttribute("totalLeaves", totalLeaves);
        model.addAttribute("approvedLeaves", approvedLeaves);
        model.addAttribute("pendingLeaves", pendingLeaves);
        model.addAttribute("rejectedLeaves", rejectedLeaves);
        model.addAttribute("sickLeaves", sickLeaves);
        model.addAttribute("vacationLeaves", vacationLeaves);
        model.addAttribute("personalLeaves", personalLeaves);
        model.addAttribute("otherLeaves", otherLeaves);

        return "admin/reports";
    }

    private Map<String, Map<String, Long>> calculateDepartmentStats(List<LeaveRequest> leaves) {
        Map<String, Map<String, Long>> stats = new HashMap<>();

        // Initialize with all departments
        List<Department> departments = departmentRepository.findAll();
        for (Department dept : departments) {
            Map<String, Long> deptStats = new HashMap<>();
            deptStats.put("total", 0L);
            deptStats.put("approved", 0L);
            deptStats.put("pending", 0L);
            deptStats.put("rejected", 0L);
            stats.put(dept.getName(), deptStats);
        }

        // Add "No Department" category
        Map<String, Long> noDeptStats = new HashMap<>();
        noDeptStats.put("total", 0L);
        noDeptStats.put("approved", 0L);
        noDeptStats.put("pending", 0L);
        noDeptStats.put("rejected", 0L);
        stats.put("No Department", noDeptStats);

        // Calculate statistics
        for (LeaveRequest leave : leaves) {
            String deptName = "No Department";
            if (leave.getEmployee() != null && leave.getEmployee().getDepartment() != null) {
                deptName = leave.getEmployee().getDepartment().getName();
            }

            Map<String, Long> deptStats = stats.get(deptName);
            if (deptStats != null) {
                deptStats.put("total", deptStats.get("total") + 1);

                switch (leave.getStatus()) {
                    case "APPROVED":
                        deptStats.put("approved", deptStats.get("approved") + 1);
                        break;
                    case "PENDING":
                        deptStats.put("pending", deptStats.get("pending") + 1);
                        break;
                    case "REJECTED":
                        deptStats.put("rejected", deptStats.get("rejected") + 1);
                        break;
                }
            }
        }

        return stats;
    }
}