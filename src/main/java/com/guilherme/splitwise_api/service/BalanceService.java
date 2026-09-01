package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.dto.DebtTransaction;
import com.guilherme.splitwise_api.model.Expense;
import com.guilherme.splitwise_api.model.ExpenseSplit;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.GroupRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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

    public List<DebtTransaction> simplifyGroupDebts(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Map<Long, BigDecimal> netBalances = new HashMap<>();
        Map<Long, String> userNames = new HashMap<>();

        for (User member : group.getMembers()) {
            netBalances.put(member.getId(), BigDecimal.ZERO);
            userNames.put(member.getId(), member.getName());
        }

        for (Expense expense : group.getExpenses()) {
            User payer = expense.getPaidBy();

            for (ExpenseSplit split : expense.getSplits()) {
                User debtor = split.getUser();

                if (!debtor.getId().equals(payer.getId())) {
                    BigDecimal amount = split.getAmountOwed();

                    netBalances.merge(debtor.getId(), amount.negate(), BigDecimal::add);
                    netBalances.merge(payer.getId(), amount, BigDecimal::add);
                }
            }
        }

        return greedySettle(netBalances, userNames);
    }

    private List<DebtTransaction> greedySettle(Map<Long, BigDecimal> netBalances, Map<Long, String> userNames) {
        List<Balance> debtors = new ArrayList<>();
        List<Balance> creditors = new ArrayList<>();

        for (Map.Entry<Long, BigDecimal> entry : netBalances.entrySet()) {
            BigDecimal amount = entry.getValue();
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new Balance(entry.getKey(), amount.abs()));
            } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new Balance(entry.getKey(), amount));
            }
        }

        debtors.sort((a, b) -> b.amount.compareTo(a.amount));
        creditors.sort((a, b) -> b.amount.compareTo(a.amount));

        List<DebtTransaction> transactions = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            Balance debtor = debtors.get(i);
            Balance creditor = creditors.get(j);

            BigDecimal settledAmount = debtor.amount.min(creditor.amount);

            transactions.add(new DebtTransaction(
                    debtor.userId, userNames.get(debtor.userId),
                    creditor.userId, userNames.get(creditor.userId),
                    settledAmount
            ));

            debtor.amount = debtor.amount.subtract(settledAmount);
            creditor.amount = creditor.amount.subtract(settledAmount);

            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) {
                i++;
            }
            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) {
                j++;
            }
        }

        return transactions;
    }

    private static class Balance {
        Long userId;
        BigDecimal amount;

        Balance(Long userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

}