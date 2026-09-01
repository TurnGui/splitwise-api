package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.dto.DebtTransaction;
import com.guilherme.splitwise_api.model.Expense;
import com.guilherme.splitwise_api.model.ExpenseSplit;
import com.guilherme.splitwise_api.model.Group;
import com.guilherme.splitwise_api.model.User;
import com.guilherme.splitwise_api.repository.GroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private BalanceService balanceService;

    private User gui;
    private User manuela;
    private User rui;
    private Group group;

    private ExpenseSplit buildSplit(User debtor, BigDecimal amount) {
        ExpenseSplit split = new ExpenseSplit();
        split.setUser(debtor);
        split.setAmountOwed(amount);
        return split;
    }

    private Expense buildExpense(User payer, ExpenseSplit... splits) {
        Expense expense = new Expense();
        expense.setPaidBy(payer);
        expense.setSplits(List.of(splits));
        return expense;
    }

    @Test
    void getGroupBalances_shouldNetOppositeDebts() {
        gui = new User();
        gui.setId(1L);
        manuela = new User();
        manuela.setId(2L);

        Expense expense1 = buildExpense(gui, buildSplit(manuela, new BigDecimal("50.00")));
        Expense expense2 = buildExpense(manuela, buildSplit(gui, new BigDecimal("30.00")));

        group = new Group();
        group.setId(1L);
        group.setExpenses(List.of(expense1, expense2));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        Map<String, BigDecimal> balances = balanceService.getGroupBalances(1L);

        assertEquals(1, balances.size());
        assertEquals(new BigDecimal("20.00"), balances.get("2-1"));
    }

    @Test
    void simplifyGroupDebts_shouldReduceChainToSingleTransaction() {
        gui = new User();
        gui.setId(1L);
        gui.setName("Gui");

        manuela = new User();
        manuela.setId(2L);
        manuela.setName("Manuela");

        rui = new User();
        rui.setId(3L);
        rui.setName("Rui");

        Expense expense1 = buildExpense(gui, buildSplit(manuela, new BigDecimal("20.00")));
        Expense expense2 = buildExpense(manuela, buildSplit(rui, new BigDecimal("20.00")));

        group = new Group();
        group.setId(1L);
        group.setMembers(List.of(gui, manuela, rui));
        group.setExpenses(List.of(expense1, expense2));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        List<DebtTransaction> transactions = balanceService.simplifyGroupDebts(1L);

        assertEquals(1, transactions.size());
        assertEquals(3L, transactions.get(0).getFromUserId());
        assertEquals(1L, transactions.get(0).getToUserId());
        assertEquals(new BigDecimal("20.00"), transactions.get(0).getAmount());
    }
}