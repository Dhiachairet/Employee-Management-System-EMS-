package com.example.employee_management_system.service;


import com.example.employee_management_system.entity.Performance;

import java.util.List;

public interface PerformanceService {
    Performance createPerformance(Performance performance);
    List<Performance> getAllPerformances();
    Performance getPerformanceById(Long id);
    Performance updatePerformance(Performance performance);
    void deletePerformance(Long id);
    List<Performance> getPerformancesByEmployee(Long employeeId);
}