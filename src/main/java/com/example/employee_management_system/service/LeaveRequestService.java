package com.example.employee_management_system.service;

import com.example.employee_management_system.entity.LeaveRequest;
import java.util.List;

public interface LeaveRequestService {
    LeaveRequest createLeaveRequest(LeaveRequest leaveRequest);
    List<LeaveRequest> getAllLeaveRequests();
    LeaveRequest getLeaveRequestById(Long id);
    LeaveRequest updateLeaveRequest(LeaveRequest leaveRequest);
    void deleteLeaveRequest(Long id);

    List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId);
    List<LeaveRequest> getLeaveRequestsByStatus(String status);

    // New methods for controllers
    List<LeaveRequest> getLeaveRequestsByEmployeeId(Long employeeId);
    List<LeaveRequest> getPendingLeaveRequests();
    void updateLeaveStatus(Long leaveId, String status);
    long countPendingLeaveRequests();
    List<LeaveRequest> getLeavesByEmployeeAndStatus(Long employeeId, String status);
}