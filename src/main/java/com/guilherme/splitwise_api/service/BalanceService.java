package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.model.Expense;
import com.guilherme.splitwise_api.model.ExpenseSplit;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class BalanceService {

    private final GroupRepository groupRepository;

    public BalanceService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public Map<String, BigDecimal> getGroupBalances(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Map<String, BigDecimal> rawBalances = new HashMap<>();

        for (Expense expense : group.getExpenses()) {
            User payer = expense.getPaidBy();

            for (ExpenseSplit split : expense.getSplits()) {
                User debtor = split.getUser();

                if (!debtor.getId().equals(payer.getId())) {
                    String key = debtor.getId() + "-" + payer.getId();
                    rawBalances.merge(key, split.getAmountOwed(), BigDecimal::add);
                }
            }
        }

        return netBalances(rawBalances);
    }

    private Map<String, BigDecimal> netBalances(Map<String, BigDecimal> rawBalances) {
        Map<String, BigDecimal> netted = new HashMap<>();
        Set<String> processed = new HashSet<>();

        for (String key : rawBalances.keySet()) {
            if (processed.contains(key)) {
                continue;
            }

            String[] parts = key.split("-");
            String debtor = parts[0];
            String creditor = parts[1];
            String oppositeKey = creditor + "-" + debtor;

            BigDecimal amount = rawBalances.get(key);
            BigDecimal oppositeAmount = rawBalances.getOrDefault(oppositeKey, BigDecimal.ZERO);

            BigDecimal net = amount.subtract(oppositeAmount);

            if (net.compareTo(BigDecimal.ZERO) > 0) {
                netted.put(debtor + "-" + creditor, net);
            } else if (net.compareTo(BigDecimal.ZERO) < 0) {
                netted.put(creditor + "-" + debtor, net.abs());
            }

            processed.add(key);
            processed.add(oppositeKey);
        }

        return netted;
    }
}