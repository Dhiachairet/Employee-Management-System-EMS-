package com.example.employee_management_system.service;


import com.example.employee_management_system.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    User updateUser(User user);
    void deleteUser(Long id);
    User findByEmail(String email);
}