package com.guilherme.splitwise_api.controller;

import com.guilherme.splitwise_api.model.ExpenseSplit;
import com.guilherme.splitwise_api.service.ExpenseSplitService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense-splits")
public class ExpenseSplitController {

    private final ExpenseSplitService expenseSplitService;

    public ExpenseSplitController(ExpenseSplitService expenseSplitService) {
        this.expenseSplitService = expenseSplitService;
    }

    @GetMapping
    public List<ExpenseSplit> getAllExpenseSplits() {
        return expenseSplitService.getAllExpenseSplits();
    }

    @PostMapping
    public ExpenseSplit createExpenseSplit(@RequestBody ExpenseSplit expenseSplit) {
        return expenseSplitService.createExpenseSplit(expenseSplit);
    }

    @GetMapping("/{id}")
    public ExpenseSplit getExpenseSplitById(@PathVariable Long id) {
        return expenseSplitService.getExpenseSplitById(id);
    }

    @PutMapping("/{id}")
    public ExpenseSplit updateExpenseSplit(@PathVariable Long id, @RequestBody ExpenseSplit updateExpenseSplit) {
        return expenseSplitService.updateExpenseSplit(id, updateExpenseSplit);
    }

    @DeleteMapping("/{id}")
    public void deleteExpenseSplit(@PathVariable Long id) {
        expenseSplitService.deleteExpenseSplit(id);
    }
}