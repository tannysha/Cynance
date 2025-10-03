package com.coms309.Cynance;

import com.coms309.Cynance.controller.ExpenseShareController;
import com.coms309.Cynance.model.ExpenseShare;
import com.coms309.Cynance.service.ExpenseShareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ExpenseShareControllerTest {

    @Mock
    private ExpenseShareService expenseShareService;

    @InjectMocks
    private ExpenseShareController expenseShareController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddExpense() {
        ExpenseShare expense = new ExpenseShare();
        expense.setId(1L);
        expense.setGroupName("TestGroup");
        expense.setDescription("Lunch");

        when(expenseShareService.addExpense(expense)).thenReturn(expense);

        ResponseEntity<ExpenseShare> response = expenseShareController.addExpense(expense);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expense, response.getBody());
    }

    @Test
    void testGetGroupExpenses() {
        String groupName = "TestGroup";
        ExpenseShare expense1 = new ExpenseShare();
        expense1.setId(1L);
        expense1.setGroupName(groupName);

        ExpenseShare expense2 = new ExpenseShare();
        expense2.setId(2L);
        expense2.setGroupName(groupName);

        List<ExpenseShare> expected = Arrays.asList(expense1, expense2);
        when(expenseShareService.getExpensesByGroup(groupName)).thenReturn(expected);

        ResponseEntity<List<ExpenseShare>> response = expenseShareController.getGroupExpenses(groupName);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
    }

    @Test
    void testSettleExpense_Success() {
        Long expenseId = 1L;
        when(expenseShareService.settleExpense(expenseId)).thenReturn(true);

        ResponseEntity<String> response = expenseShareController.settleExpense(expenseId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Expense settled successfully.", response.getBody());
    }

    @Test
    void testSettleExpense_Failure() {
        Long expenseId = 999L;
        when(expenseShareService.settleExpense(expenseId)).thenReturn(false);

        ResponseEntity<String> response = expenseShareController.settleExpense(expenseId);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Expense not found.", response.getBody());
    }
}

