package com.example.employee_management_system.entity;



import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Performance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate reviewDate;
    private Double rating;
    private String comments;
    private String goals;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    public Performance() {}

    public Performance(LocalDate reviewDate, Double rating, String comments, String goals) {
        this.reviewDate = reviewDate;
        this.rating = rating;
        this.comments = comments;
        this.goals = goals;
    }
}