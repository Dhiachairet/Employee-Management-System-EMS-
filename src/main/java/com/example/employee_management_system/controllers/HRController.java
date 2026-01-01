package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.LeaveRequest;
import com.example.employee_management_system.service.EmployeeService;
import com.example.employee_management_system.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/hr")
@PreAuthorize("hasAnyRole('HR', 'ADMIN')")
public class HRController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/dashboard")
    public String hrDashboard(Model model) {
        // Get pending leave requests for HR dashboard
        List<LeaveRequest> pendingLeaves = leaveRequestService.getPendingLeaveRequests();
        long totalEmployees = employeeService.getTotalEmployees();

        model.addAttribute("pendingLeaves", pendingLeaves);
        model.addAttribute("totalEmployees", totalEmployees);

        return "hr/dashboard";
    }

    @GetMapping("/leaves")
    public String manageLeaves(Model model) {
        List<LeaveRequest> allLeaves = leaveRequestService.getAllLeaveRequests();
        model.addAttribute("leaveRequests", allLeaves);
        return "hr/leaves";
    }

    @PostMapping("/leaves/approve/{id}")
    public String approveLeave(@PathVariable Long id) {
        leaveRequestService.updateLeaveStatus(id, "APPROVED");
        return "redirect:/hr/leaves";
    }

    @PostMapping("/leaves/reject/{id}")
    public String rejectLeave(@PathVariable Long id) {
        leaveRequestService.updateLeaveStatus(id, "REJECTED");
        return "redirect:/hr/leaves";
    }

    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "hr/employees";
    }
}