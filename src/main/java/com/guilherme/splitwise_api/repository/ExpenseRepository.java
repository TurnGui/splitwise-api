package com.guilherme.splitwise_api.repository;

import com.guilherme.splitwise_api.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long>{
}
