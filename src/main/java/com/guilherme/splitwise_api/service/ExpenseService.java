package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.model.Expense;
import com.guilherme.splitwise_api.model.ExpenseSplit;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.ExpenseRepository;
import com.guilherme.splitwise_api.repository.ExpenseSplitRepository;
import com.guilherme.splitwise_api.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final GroupRepository groupRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public ExpenseService(ExpenseRepository expenseRepository, GroupRepository groupRepository, ExpenseSplitRepository expenseSplitRepository) {
        this.groupRepository = groupRepository;
        this.expenseRepository = expenseRepository;
        this.expenseSplitRepository = expenseSplitRepository;
    }

    public Expense createExpense(Expense expense) {
        Expense savedExpense = expenseRepository.save(expense);

        Group group = groupRepository.findById(expense.getGroup().getId())
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<User> members = group.getMembers();
        int numberOfMembers = members.size();

        BigDecimal totalAmount = savedExpense.getAmount();
        BigDecimal baseAmount = totalAmount.divide(
                BigDecimal.valueOf(numberOfMembers), 2, RoundingMode.DOWN
        );
        BigDecimal distributed = baseAmount.multiply(BigDecimal.valueOf(numberOfMembers));
        BigDecimal remainder = totalAmount.subtract(distributed);

        BigDecimal cent = new BigDecimal("0.01");
        int centsToDistribute = remainder.divide(cent).intValue();

        for (int i = 0; i < numberOfMembers; i++) {
            BigDecimal amountOwed = baseAmount;
            if (i < centsToDistribute) {
                amountOwed = amountOwed.add(cent);
            }

            ExpenseSplit split = new ExpenseSplit();
            split.setExpense(savedExpense);
            split.setUser(members.get(i));
            split.setAmountOwed(amountOwed);
            expenseSplitRepository.save(split);
        }

        return savedExpense;
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