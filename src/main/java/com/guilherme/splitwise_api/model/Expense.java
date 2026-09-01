package com.guilherme.splitwise_api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    @JsonBackReference("group-expenses")
    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @JsonBackReference("user-expenses-paid")
    @ManyToOne
    @JoinColumn(name = "paid_by_user_id", nullable = false)
    private User paidBy;

    @JsonManagedReference("expense-splits")
    @OneToMany(mappedBy = "expense")
    private List<ExpenseSplit> splits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;
}