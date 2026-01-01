package com.example.employee_management_system.service;

import com.example.employee_management_system.entity.LeaveRequest;
import com.example.employee_management_system.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Override
    public LeaveRequest createLeaveRequest(LeaveRequest leaveRequest) {
        return leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    @Override
    public LeaveRequest getLeaveRequestById(Long id) {
        return leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave request not found with id: " + id));
    }

    @Override
    public LeaveRequest updateLeaveRequest(LeaveRequest leaveRequest) {
        // Check if leave request exists
        LeaveRequest existingLeave = getLeaveRequestById(leaveRequest.getId());

        // Update fields
        existingLeave.setStartDate(leaveRequest.getStartDate());
        existingLeave.setEndDate(leaveRequest.getEndDate());
        existingLeave.setLeaveType(leaveRequest.getLeaveType());
        existingLeave.setReason(leaveRequest.getReason());
        existingLeave.setStatus(leaveRequest.getStatus());

        // Update employee if provided
        if (leaveRequest.getEmployee() != null) {
            existingLeave.setEmployee(leaveRequest.getEmployee());
        }

        return leaveRequestRepository.save(existingLeave);
    }

    @Override
    public void deleteLeaveRequest(Long id) {
        LeaveRequest leaveRequest = getLeaveRequestById(id);
        leaveRequestRepository.delete(leaveRequest);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByStatus(String status) {
        return leaveRequestRepository.findByStatus(status);
    }

    @Override
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(Long employeeId) {
        // This is essentially the same as getLeaveRequestsByEmployee
        // You can either implement it the same way or add custom logic
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<LeaveRequest> getPendingLeaveRequests() {
        return leaveRequestRepository.findByStatus("PENDING");
    }

    @Override
    public void updateLeaveStatus(Long leaveId, String status) {
        LeaveRequest leaveRequest = getLeaveRequestById(leaveId);
        leaveRequest.setStatus(status);
        leaveRequestRepository.save(leaveRequest);
    }

    @Override
    public long countPendingLeaveRequests() {
        return leaveRequestRepository.countByStatus("PENDING");
    }

    @Override
    public List<LeaveRequest> getLeavesByEmployeeAndStatus(Long employeeId, String status) {
        return leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, status);
    }
}