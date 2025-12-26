package com.example.employee_management_system.service;


import com.example.employee_management_system.entity.Performance;
import com.example.employee_management_system.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Override
    public Performance createPerformance(Performance performance) {
        return performanceRepository.save(performance);
    }

    @Override
    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    @Override
    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id).orElse(null);
    }

    @Override
    public Performance updatePerformance(Performance performance) {
        return performanceRepository.save(performance);
    }

    @Override
    public void deletePerformance(Long id) {
        performanceRepository.deleteById(id);
    }

    @Override
    public List<Performance> getPerformancesByEmployee(Long employeeId) {
        return performanceRepository.findByEmployeeId(employeeId);
    }
}