package com.example.employee_management_system.repository;


import com.example.employee_management_system.entity.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    List<Performance> findByEmployeeId(Long employeeId);
}