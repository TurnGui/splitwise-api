package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.model.Expense;
import com.guilherme.splitwise_api.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Expense getExpenseById(Long id) {
        Optional<Expense> resultado = expenseRepository.findById(id);
        if (resultado.isPresent()) {
            return resultado.get();
        } else {
            throw new RuntimeException("Expense not found with id:" + id);
        }
    }

    public Expense updateExpense(Long id, Expense updateExpense) {
        Optional<Expense> resultado = expenseRepository.findById(id);
        if (resultado.isPresent()) {
            Expense expense = resultado.get();
            expense.setDescription(updateExpense.getDescription());
            expense.setAmount(updateExpense.getAmount());
            expense.setDate(updateExpense.getDate());
            expense.setGroup(updateExpense.getGroup());
            expense.setPaidBy(updateExpense.getPaidBy());
            return expenseRepository.save(expense);
        } else {
            throw new RuntimeException("Expense not found with id:" + id);
        }
    }

    public void deleteExpense(Long id) {
        Optional<Expense> resultado = expenseRepository.findById(id);
        if (resultado.isPresent()) {
            expenseRepository.deleteById(id);
        } else {
            throw new RuntimeException("Expense with id" + id + "does not exist.");
        }
    }
}