package com.example.employee_management_system.repository;




import com.example.employee_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    List<User> findByRole(String role);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User chercherParEmail(@Param("email") String email);
}