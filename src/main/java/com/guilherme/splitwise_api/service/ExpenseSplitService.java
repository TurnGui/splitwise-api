package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.model.ExpenseSplit;
import com.guilherme.splitwise_api.repository.ExpenseSplitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseSplitService {

    private final ExpenseSplitRepository expenseSplitRepository;

    public ExpenseSplitService(ExpenseSplitRepository expenseSplitRepository) {
        this.expenseSplitRepository = expenseSplitRepository;
    }

    public ExpenseSplit createExpenseSplit(ExpenseSplit expenseSplit) {
        return expenseSplitRepository.save(expenseSplit);
    }

    public List<ExpenseSplit> getAllExpenseSplits() {
        return expenseSplitRepository.findAll();
    }

    public ExpenseSplit getExpenseSplitById(Long id) {
        Optional<ExpenseSplit> resultado = expenseSplitRepository.findById(id);
        if (resultado.isPresent()) {
            return resultado.get();
        } else {
            throw new RuntimeException("ExpenseSplit not found with id:" + id);
        }
    }

    public ExpenseSplit updateExpenseSplit(Long id, ExpenseSplit updateExpenseSplit) {
        Optional<ExpenseSplit> resultado = expenseSplitRepository.findById(id);
        if (resultado.isPresent()) {
            ExpenseSplit expenseSplit = resultado.get();
            expenseSplit.setExpense(updateExpenseSplit.getExpense());
            expenseSplit.setUser(updateExpenseSplit.getUser());
            expenseSplit.setAmountOwed(updateExpenseSplit.getAmountOwed());
            return expenseSplitRepository.save(expenseSplit);
        } else {
            throw new RuntimeException("ExpenseSplit not found with id:" + id);
        }
    }

    public void deleteExpenseSplit(Long id) {
        Optional<ExpenseSplit> resultado = expenseSplitRepository.findById(id);
        if (resultado.isPresent()) {
            expenseSplitRepository.deleteById(id);
        } else {
            throw new RuntimeException("ExpenseSplit with id" + id + "does not exist.");
        }
    }
}