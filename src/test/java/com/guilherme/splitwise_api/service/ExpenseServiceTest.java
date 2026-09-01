package com.guilherme.splitwise_api.service;

import com.guilherme.splitwise_api.dto.CreateExpenseRequest;
import com.guilherme.splitwise_api.dto.SplitDetail;
import com.guilherme.splitwise_api.exception.InvalidRequestException;
import com.guilherme.splitwise_api.model.*;
import com.guilherme.splitwise_api.repository.ExpenseRepository;
import com.guilherme.splitwise_api.repository.ExpenseSplitRepository;
import com.guilherme.splitwise_api.repository.GroupRepository;
import com.guilherme.splitwise_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private ExpenseSplitRepository expenseSplitRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    @Captor
    private ArgumentCaptor<ExpenseSplit> splitCaptor;

    private User user1;
    private User user2;
    private Group group;

    private void setupCommon() {
        user1 = new User();
        user1.setId(1L);
        user1.setName("Gui");

        user2 = new User();
        user2.setId(2L);
        user2.setName("Manuela");

        group = new Group();
        group.setId(1L);
        group.setMembers(List.of(user1, user2));

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
    }

    @Test
    void createExpense_withEqualSplit_shouldDivideAmountEvenly() {
        setupCommon();

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setDescription("Dinner");
        request.setAmount(new BigDecimal("100.00"));
        request.setDate(LocalDate.now());
        request.setGroupId(1L);
        request.setPaidById(1L);
        request.setSplitType(SplitType.EQUAL);

        Expense savedExpense = new Expense();
        savedExpense.setId(10L);
        savedExpense.setAmount(new BigDecimal("100.00"));
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        expenseService.createExpense(request);

        verify(expenseSplitRepository, times(2)).save(splitCaptor.capture());
        List<ExpenseSplit> splits = splitCaptor.getAllValues();

        assertEquals(new BigDecimal("50.00"), splits.get(0).getAmountOwed());
        assertEquals(new BigDecimal("50.00"), splits.get(1).getAmountOwed());
    }

    @Test
    void createExpense_withPercentageSplit_notAddingTo100_shouldThrow() {
        setupCommon();

        CreateExpenseRequest request = new CreateExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setGroupId(1L);
        request.setPaidById(1L);
        request.setSplitType(SplitType.PERCENTAGE);
        request.setSplitDetails(List.of(
                new SplitDetail(1L, new BigDecimal("60")),
                new SplitDetail(2L, new BigDecimal("30"))
        ));

        Expense savedExpense = new Expense();
        savedExpense.setAmount(new BigDecimal("100.00"));
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        assertThrows(InvalidRequestException.class, () -> expenseService.createExpense(request));
    }
}