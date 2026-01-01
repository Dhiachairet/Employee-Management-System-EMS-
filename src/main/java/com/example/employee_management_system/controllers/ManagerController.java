package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.entity.LeaveRequest;
import com.example.employee_management_system.entity.Performance;
import com.example.employee_management_system.service.EmployeeService;
import com.example.employee_management_system.service.LeaveRequestService;
import com.example.employee_management_system.service.PerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
public class ManagerController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private PerformanceService performanceService;

    @GetMapping("/dashboard")
    public String managerDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            // Get manager's own info
            Employee manager = employeeService.getEmployeeByEmail(email);
            model.addAttribute("manager", manager);

            // Get manager's team (employees in same department)
            if (manager.getDepartment() != null) {
                List<Employee> teamMembers = employeeService.getEmployeesByDepartment(manager.getDepartment().getId());

                // Filter out the manager from the team list
                teamMembers.removeIf(e -> e.getId().equals(manager.getId()));

                model.addAttribute("teamMembers", teamMembers);
                model.addAttribute("teamSize", teamMembers.size());

                // Get pending leaves for team members
                long pendingLeavesCount = 0;
                for (Employee teamMember : teamMembers) {
                    List<LeaveRequest> pendingLeaves = leaveRequestService.getLeavesByEmployeeAndStatus(
                            teamMember.getId(), "PENDING");
                    pendingLeavesCount += pendingLeaves.size();
                }
                model.addAttribute("pendingLeavesCount", pendingLeavesCount);
            }

        } catch (Exception e) {
            model.addAttribute("error", "Could not load dashboard data: " + e.getMessage());
        }

        return "manager/dashboard";
    }

    @GetMapping("/team")
    public String viewTeam(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee manager = employeeService.getEmployeeByEmail(email);

            if (manager.getDepartment() != null) {
                List<Employee> teamMembers = employeeService.getEmployeesByDepartment(manager.getDepartment().getId());
                // Filter out the manager from the team list
                teamMembers.removeIf(e -> e.getId().equals(manager.getId()));

                model.addAttribute("teamMembers", teamMembers);
                model.addAttribute("department", manager.getDepartment());
            } else {
                model.addAttribute("message", "You are not assigned to any department.");
            }

        } catch (Exception e) {
            model.addAttribute("error", "Could not load team data: " + e.getMessage());
        }

        return "manager/team";
    }

    @GetMapping("/leaves")
    public String manageTeamLeaves(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee manager = employeeService.getEmployeeByEmail(email);

            if (manager.getDepartment() != null) {
                // Get all employees in the department
                List<Employee> teamMembers = employeeService.getEmployeesByDepartment(manager.getDepartment().getId());

                // Get all leave requests for team members
                List<LeaveRequest> teamLeaves = leaveRequestService.getAllLeaveRequests();

                // Filter leaves to show only team member leaves (not manager's own leaves)
                teamLeaves.removeIf(leave ->
                        leave.getEmployee().getId().equals(manager.getId()) ||
                                !teamMembers.contains(leave.getEmployee())
                );

                model.addAttribute("teamLeaves", teamLeaves);
                model.addAttribute("teamMembers", teamMembers);
            }

        } catch (Exception e) {
            model.addAttribute("error", "Could not load leave requests: " + e.getMessage());
        }

        return "manager/leaves";
    }

    @PostMapping("/leaves/approve/{id}")
    public String approveTeamLeave(@PathVariable Long id) {
        leaveRequestService.updateLeaveStatus(id, "APPROVED");
        return "redirect:/manager/leaves?success=Leave approved";
    }

    @PostMapping("/leaves/reject/{id}")
    public String rejectTeamLeave(@PathVariable Long id) {
        leaveRequestService.updateLeaveStatus(id, "REJECTED");
        return "redirect:/manager/leaves?success=Leave rejected";
    }

    @GetMapping("/performance")
    public String managePerformance(Model model, Authentication authentication) {
        String email = authentication.getName();

        try {
            Employee manager = employeeService.getEmployeeByEmail(email);

            if (manager.getDepartment() != null) {
                List<Employee> teamMembers = employeeService.getEmployeesByDepartment(manager.getDepartment().getId());
                // Filter out the manager
                teamMembers.removeIf(e -> e.getId().equals(manager.getId()));

                model.addAttribute("teamMembers", teamMembers);
            }

        } catch (Exception e) {
            model.addAttribute("error", "Could not load performance data: " + e.getMessage());
        }

        return "manager/performance";
    }

    @GetMapping("/performance/add/{employeeId}")
    public String addPerformanceReview(@PathVariable Long employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        Performance performance = new Performance();

        model.addAttribute("performance", performance);
        model.addAttribute("employee", employee);

        return "manager/add_performance";
    }

    @PostMapping("/performance/save")
    public String savePerformanceReview(@ModelAttribute Performance performance,
                                        @RequestParam Long employeeId) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        performance.setEmployee(employee);
        performanceService.createPerformance(performance);

        return "redirect:/manager/performance?success=Performance review added";
    }

    @GetMapping("/performance/view/{employeeId}")
    public String viewEmployeePerformance(@PathVariable Long employeeId, Model model) {
        Employee employee = employeeService.getEmployeeById(employeeId);
        List<Performance> performanceReviews = performanceService.getPerformanceByEmployeeId(employeeId);

        model.addAttribute("employee", employee);
        model.addAttribute("performanceReviews", performanceReviews);

        return "manager/view_performance";
    }
}