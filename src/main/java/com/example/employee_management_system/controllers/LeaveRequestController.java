package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.LeaveRequest;
import com.example.employee_management_system.entity.Employee;
import com.example.employee_management_system.service.LeaveRequestService;
import com.example.employee_management_system.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/all")
    public String listLeaveRequests(Model model) {
        List<LeaveRequest> listLeaveRequests = leaveRequestService.getAllLeaveRequests();
        model.addAttribute("listLeaveRequests", listLeaveRequests);
        return "liste_leave_requests";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        LeaveRequest leaveRequest = new LeaveRequest();
        List<Employee> employees = employeeService.getAllEmployees();

        model.addAttribute("leaveRequest", leaveRequest);
        model.addAttribute("employees", employees);
        return "new_leave_request";
    }

    @PostMapping("/save")
    public String saveLeaveRequest(@ModelAttribute("leaveRequest") LeaveRequest leaveRequest) {
        leaveRequestService.createLeaveRequest(leaveRequest);
        return "redirect:/leave-requests/all";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        LeaveRequest leaveRequest = leaveRequestService.getLeaveRequestById(id);
        List<Employee> employees = employeeService.getAllEmployees();

        model.addAttribute("leaveRequest", leaveRequest);
        model.addAttribute("employees", employees);
        return "update_leave_request";
    }

    @PostMapping("/update/{id}")
    public String updateLeaveRequest(@PathVariable("id") Long id, @ModelAttribute("leaveRequest") LeaveRequest leaveRequest) {
        leaveRequest.setId(id);
        leaveRequestService.updateLeaveRequest(leaveRequest);
        return "redirect:/leave-requests/all";
    }

    @GetMapping("/delete/{id}")
    public String deleteLeaveRequest(@PathVariable("id") Long id) {
        leaveRequestService.deleteLeaveRequest(id);
        return "redirect:/leave-requests/all";
    }
}