package com.guilherme.splitwise_api.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany(mappedBy = "members")
    private List<Group> groups;

    @JsonManagedReference("user-expenses-paid")
    @OneToMany(mappedBy = "paidBy")
    private List<Expense> expensesPaid;

    @JsonManagedReference("user-splits")
    @OneToMany(mappedBy = "user")
    private List<ExpenseSplit> expenseSplits;
}