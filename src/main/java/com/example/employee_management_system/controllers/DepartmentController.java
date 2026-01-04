package com.example.employee_management_system.controllers;

import com.example.employee_management_system.entity.Department;
import com.example.employee_management_system.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/all")
    public String listDepartments(Model model) {
        List<Department> listDepartments = departmentService.getAllDepartments();
        model.addAttribute("listDepartments", listDepartments);
        return "liste_departments";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Department department = new Department();
        model.addAttribute("department", department);
        return "new_department";
    }

    @PostMapping("/save")
    public String saveDepartment(@ModelAttribute("department") Department department) {
        departmentService.createDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Department department = departmentService.getDepartmentById(id);
        model.addAttribute("department", department);
        return "update_department";
    }

    @PostMapping("/update/{id}")
    public String updateDepartment(@PathVariable("id") Long id, @ModelAttribute("department") Department department) {
        department.setId(id);
        departmentService.updateDepartment(department);
        return "redirect:/admin/departments";
    }

    @GetMapping("/delete/{id}")
    public String deleteDepartment(@PathVariable("id") Long id) {
        try {
            departmentService.deleteDepartment(id);
            return "redirect:/admin/departments?success=Department deleted successfully";
        } catch (RuntimeException e) {
            // Handle the exception by redirecting with an error message
            return "redirect:/admin/departments?error=" + encodeErrorMessage(e.getMessage());
        }
    }

    // Helper method to encode error messages for URL
    private String encodeErrorMessage(String message) {
        if (message == null) return "";
        return message.replace(" ", "%20")
                .replace("[", "%5B")
                .replace("]", "%5D")
                .replace("(", "%28")
                .replace(")", "%29");
    }
}