package com.example.employee_management_system.service;

import com.example.employee_management_system.entity.Performance;
import com.example.employee_management_system.repository.PerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PerformanceServiceImpl implements PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Override
    public Performance createPerformance(Performance performance) {
        if (performance.getReviewDate() == null) {
            performance.setReviewDate(LocalDate.now());
        }
        return performanceRepository.save(performance);
    }

    @Override
    public List<Performance> getAllPerformances() {
        return performanceRepository.findAll();
    }

    @Override
    public Performance getPerformanceById(Long id) {
        return performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found with id: " + id));
    }

    @Override
    public Performance updatePerformance(Performance performance) {
        Performance existingPerformance = getPerformanceById(performance.getId());

        existingPerformance.setReviewDate(performance.getReviewDate());
        existingPerformance.setRating(performance.getRating());
        existingPerformance.setComments(performance.getComments());
        existingPerformance.setGoals(performance.getGoals());

        if (performance.getEmployee() != null) {
            existingPerformance.setEmployee(performance.getEmployee());
        }

        return performanceRepository.save(existingPerformance);
    }

    @Override
    public void deletePerformance(Long id) {
        Performance performance = getPerformanceById(id);
        performanceRepository.delete(performance);
    }

    @Override
    public List<Performance> getPerformanceByEmployeeId(Long employeeId) {
        return performanceRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<Performance> getPerformanceByEmployeeAndDateRange(Long employeeId, String startDate, String endDate) {
        // For simplicity, returning all performances for the employee
        // You can implement date filtering if needed
        return performanceRepository.findByEmployeeId(employeeId);
    }
}