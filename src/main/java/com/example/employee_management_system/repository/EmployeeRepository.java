package com.example.employee_management_system.repository;


import com.example.employee_management_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // findByEmployeeId REMOVED
    List<Employee> findByDepartmentId(Long departmentId);

    @Query("SELECT e FROM Employee e WHERE e.position LIKE %:position%")
    List<Employee> chercherParPosition(@Param("position") String position);

    List<Employee> findByRole(String role);
}