package com.guilherme.splitwise_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.nio.file.attribute.UserPrincipal;

public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal amountOwed;
}
