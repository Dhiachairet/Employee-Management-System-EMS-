package com.example.employee_management_system.repository;

import com.example.employee_management_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    List<Employee> findByDepartmentId(Long departmentId);

    @Query("SELECT e FROM Employee e JOIN e.roles r WHERE r.name = :roleName")
    List<Employee> findByRoleName(@Param("roleName") String roleName);

    long countByDepartmentId(Long departmentId);
    boolean existsByEmail(String email);
}