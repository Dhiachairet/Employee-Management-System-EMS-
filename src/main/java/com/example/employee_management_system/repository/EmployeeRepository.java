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

    // Remove or fix findByRole method
    // List<Employee> findByRole(String role);  // <-- REMOVE THIS

    // Add this instead if you need to find by role:
    @Query("SELECT e FROM Employee e JOIN e.roles r WHERE r.name = :roleName")
    List<Employee> findByRoleName(@Param("roleName") String roleName);

    // Or add this if you add a 'role' field to Employee entity:
    // List<Employee> findByRole(String role);

    // Other useful methods:
    List<Employee> findByDepartmentId(Long departmentId);
    Optional<Employee> findByEmail(String email);
}