package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.dto.CreateExpenseRequest;
import com.guilherme.splitwise_api.dto.SplitDetail;
import com.guilherme.splitwise_api.model.*;
import com.guilherme.splitwise_api.repository.ExpenseRepository;
import com.guilherme.splitwise_api.repository.ExpenseSplitRepository;
import com.guilherme.splitwise_api.repository.GroupRepository;
import com.guilherme.splitwise_api.repository.UserRepository;
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
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, GroupRepository groupRepository,
                          ExpenseSplitRepository expenseSplitRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.expenseSplitRepository = expenseSplitRepository;
        this.userRepository = userRepository;
    }

    public Expense createExpense(CreateExpenseRequest request) {
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        User paidBy = userRepository.findById(request.getPaidById())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setGroup(group);
        expense.setPaidBy(paidBy);
        expense.setSplitType(request.getSplitType());

        Expense savedExpense = expenseRepository.save(expense);

        switch (request.getSplitType()) {
            case EQUAL -> createEqualSplits(savedExpense, group.getMembers());
            case PERCENTAGE -> createPercentageSplits(savedExpense, request.getSplitDetails());
            case EXACT -> createExactSplits(savedExpense, request.getSplitDetails());
        }

        return savedExpense;
    }

    private void createEqualSplits(Expense expense, List<User> members) {
        int numberOfMembers = members.size();
        BigDecimal totalAmount = expense.getAmount();

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
            saveSplit(expense, members.get(i), amountOwed);
        }
    }

    private void createPercentageSplits(Expense expense, List<SplitDetail> splitDetails) {
        BigDecimal totalPercentage = BigDecimal.ZERO;
        for (SplitDetail detail : splitDetails) {
            totalPercentage = totalPercentage.add(detail.getValue());
        }

        if (totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new RuntimeException("Percentages must add up to 100, but they add up to " + totalPercentage);
        }

        BigDecimal totalAmount = expense.getAmount();

        for (SplitDetail detail : splitDetails) {
            User user = userRepository.findById(detail.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            BigDecimal percentage = detail.getValue();
            BigDecimal amountOwed = totalAmount
                    .multiply(percentage)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            saveSplit(expense, user, amountOwed);
        }
    }

    private void createExactSplits(Expense expense, List<SplitDetail> splitDetails) {
        BigDecimal totalSplit = BigDecimal.ZERO;
        for (SplitDetail detail : splitDetails) {
            totalSplit = totalSplit.add(detail.getValue());
        }

        if (totalSplit.compareTo(expense.getAmount()) != 0) {
            throw new RuntimeException("Split amounts must add up to the expense total (" +
                    expense.getAmount() + "), but they add up to " + totalSplit);
        }

        for (SplitDetail detail : splitDetails) {
            User user = userRepository.findById(detail.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            saveSplit(expense, user, detail.getValue());
        }
    }

    private void saveSplit(Expense expense, User user, BigDecimal amountOwed) {
        ExpenseSplit split = new ExpenseSplit();
        split.setExpense(expense);
        split.setUser(user);
        split.setAmountOwed(amountOwed);
        expenseSplitRepository.save(split);
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