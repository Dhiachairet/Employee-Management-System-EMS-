package com.example.employee_management_system.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveType;
    private String reason;
    private String status;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    public LeaveRequest() {}

    public LeaveRequest(LocalDate startDate, LocalDate endDate, String leaveType, String reason, String status) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.reason = reason;
        this.status = status;
    }


    public long calculateDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}