package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.entity.LeaveRequest;
import com.example.employee_management_system.service.EmployeeService;
import com.example.employee_management_system.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/employee")
@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR', 'ADMIN')")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @GetMapping("/dashboard")
    public String employeeDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee employee = employeeService.getEmployeeByEmail(email);
            model.addAttribute("employee", employee);

            List<LeaveRequest> myLeaves = leaveRequestService.getLeaveRequestsByEmployeeId(employee.getId());
            model.addAttribute("myLeaves", myLeaves != null ? myLeaves : new ArrayList<>());

            // Calculate statistics
            if (myLeaves != null && !myLeaves.isEmpty()) {
                long totalLeaves = myLeaves.size();
                long approvedLeaves = myLeaves.stream()
                        .filter(l -> "APPROVED".equals(l.getStatus()))
                        .count();
                long pendingLeaves = myLeaves.stream()
                        .filter(l -> "PENDING".equals(l.getStatus()))
                        .count();
                long rejectedLeaves = myLeaves.stream()
                        .filter(l -> "REJECTED".equals(l.getStatus()))
                        .count();

                // Count by type
                long sickLeaves = myLeaves.stream()
                        .filter(l -> "SICK".equals(l.getLeaveType()))
                        .count();
                long vacationLeaves = myLeaves.stream()
                        .filter(l -> "VACATION".equals(l.getLeaveType()))
                        .count();
                long otherLeaves = totalLeaves - sickLeaves - vacationLeaves;

                // Calculate percentages
                int sickPercentage = totalLeaves > 0 ? (int) Math.round((sickLeaves * 100.0) / totalLeaves) : 0;
                int vacationPercentage = totalLeaves > 0 ? (int) Math.round((vacationLeaves * 100.0) / totalLeaves) : 0;
                int otherPercentage = totalLeaves > 0 ? (int) Math.round((otherLeaves * 100.0) / totalLeaves) : 0;

                model.addAttribute("totalLeaves", totalLeaves);
                model.addAttribute("approvedLeaves", approvedLeaves);
                model.addAttribute("pendingLeaves", pendingLeaves);
                model.addAttribute("rejectedLeaves", rejectedLeaves);
                model.addAttribute("sickLeaves", sickLeaves);
                model.addAttribute("vacationLeaves", vacationLeaves);
                model.addAttribute("otherLeaves", otherLeaves);
                model.addAttribute("sickPercentage", sickPercentage);
                model.addAttribute("vacationPercentage", vacationPercentage);
                model.addAttribute("otherPercentage", otherPercentage);
            } else {
                // Set default values
                model.addAttribute("totalLeaves", 0);
                model.addAttribute("approvedLeaves", 0);
                model.addAttribute("pendingLeaves", 0);
                model.addAttribute("rejectedLeaves", 0);
                model.addAttribute("sickLeaves", 0);
                model.addAttribute("vacationLeaves", 0);
                model.addAttribute("otherLeaves", 0);
                model.addAttribute("sickPercentage", 0);
                model.addAttribute("vacationPercentage", 0);
                model.addAttribute("otherPercentage", 0);
            }

        } catch (Exception e) {
            // Create a dummy employee object with basic info
            Employee dummyEmployee = new Employee();
            dummyEmployee.setEmail(email);
            dummyEmployee.setPosition("Employee");
            dummyEmployee.setSalary(0.0);
            model.addAttribute("employee", dummyEmployee);
            model.addAttribute("myLeaves", new ArrayList<>());

            // Set default values for stats
            model.addAttribute("totalLeaves", 0);
            model.addAttribute("approvedLeaves", 0);
            model.addAttribute("pendingLeaves", 0);
            model.addAttribute("rejectedLeaves", 0);
            model.addAttribute("sickLeaves", 0);
            model.addAttribute("vacationLeaves", 0);
            model.addAttribute("otherLeaves", 0);
            model.addAttribute("sickPercentage", 0);
            model.addAttribute("vacationPercentage", 0);
            model.addAttribute("otherPercentage", 0);
        }

        return "employee/dashboard";
    }
    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee employee = employeeService.getEmployeeByEmail(email);
            model.addAttribute("employee", employee);
        } catch (Exception e) {
            // Create a dummy employee object with basic info
            Employee dummyEmployee = new Employee();
            dummyEmployee.setEmail(email);
            dummyEmployee.setPosition("Employee");
            dummyEmployee.setSalary(0.0);
            model.addAttribute("employee", dummyEmployee);
        }

        return "employee/profile";
    }

    @GetMapping("/leaves")
    public String myLeaves(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee employee = employeeService.getEmployeeByEmail(email);
            List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeId(employee.getId());

            model.addAttribute("employee", employee);
            model.addAttribute("leaveRequests", leaveRequests != null ? leaveRequests : new ArrayList<>());

            // Calculate statistics
            if (leaveRequests != null && !leaveRequests.isEmpty()) {
                long totalLeaves = leaveRequests.size();
                long approvedLeaves = leaveRequests.stream()
                        .filter(l -> "APPROVED".equals(l.getStatus()))
                        .count();
                long pendingLeaves = leaveRequests.stream()
                        .filter(l -> "PENDING".equals(l.getStatus()))
                        .count();
                long rejectedLeaves = leaveRequests.stream()
                        .filter(l -> "REJECTED".equals(l.getStatus()))
                        .count();

                // Count by type
                long sickLeaves = leaveRequests.stream()
                        .filter(l -> "SICK".equals(l.getLeaveType()))
                        .count();
                long vacationLeaves = leaveRequests.stream()
                        .filter(l -> "VACATION".equals(l.getLeaveType()))
                        .count();
                long otherLeaves = totalLeaves - sickLeaves - vacationLeaves;

                model.addAttribute("totalLeaves", totalLeaves);
                model.addAttribute("approvedLeaves", approvedLeaves);
                model.addAttribute("pendingLeaves", pendingLeaves);
                model.addAttribute("rejectedLeaves", rejectedLeaves);
                model.addAttribute("sickLeaves", sickLeaves);
                model.addAttribute("vacationLeaves", vacationLeaves);
                model.addAttribute("otherLeaves", otherLeaves);
            } else {
                // Set default values
                model.addAttribute("totalLeaves", 0);
                model.addAttribute("approvedLeaves", 0);
                model.addAttribute("pendingLeaves", 0);
                model.addAttribute("rejectedLeaves", 0);
                model.addAttribute("sickLeaves", 0);
                model.addAttribute("vacationLeaves", 0);
                model.addAttribute("otherLeaves", 0);
            }

        } catch (Exception e) {
            model.addAttribute("leaveRequests", new ArrayList<>());
            model.addAttribute("message", "Please complete your employee profile to view leaves.");
            model.addAttribute("totalLeaves", 0);
            model.addAttribute("approvedLeaves", 0);
            model.addAttribute("pendingLeaves", 0);
            model.addAttribute("rejectedLeaves", 0);
        }

        return "employee/leaves";
    }

    @GetMapping("/leaves/apply")
    public String applyLeaveForm(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee employee = employeeService.getEmployeeByEmail(email);
            model.addAttribute("leaveRequest", new LeaveRequest());
            model.addAttribute("employee", employee);
            return "employee/apply_leave";
        } catch (Exception e) {
            model.addAttribute("message", "Please complete your employee profile first");
            return "redirect:/employee/leaves?error=Please complete your employee profile first";
        }
    }

    @PostMapping("/leaves/apply")
    public String applyLeave(@ModelAttribute LeaveRequest leaveRequest,
                             Authentication authentication,
                             Model model) {
        String email = authentication.getName();

        try {
            Employee employee = employeeService.getEmployeeByEmail(email);

            // Validate dates
            if (leaveRequest.getStartDate() == null || leaveRequest.getEndDate() == null) {
                model.addAttribute("error", "Please select both start and end dates");
                model.addAttribute("employee", employee);
                model.addAttribute("leaveRequest", leaveRequest);
                return "employee/apply_leave";
            }

            // Validate end date is not before start date
            if (leaveRequest.getEndDate().isBefore(leaveRequest.getStartDate())) {
                model.addAttribute("error", "End date cannot be before start date");
                model.addAttribute("employee", employee);
                model.addAttribute("leaveRequest", leaveRequest);
                return "employee/apply_leave";
            }

            // Set employee and status
            leaveRequest.setEmployee(employee);
            leaveRequest.setStatus("PENDING");

            // Save leave request
            leaveRequestService.createLeaveRequest(leaveRequest);

            return "redirect:/employee/leaves?success=Leave applied successfully! It will be reviewed by HR.";

        } catch (Exception e) {
            return "redirect:/employee/leaves?error=Could not apply leave. Please try again.";
        }
    }
}