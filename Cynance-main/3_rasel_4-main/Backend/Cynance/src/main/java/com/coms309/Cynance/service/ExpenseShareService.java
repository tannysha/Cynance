package com.coms309.Cynance.service;

import com.coms309.Cynance.model.ExpenseShare;
import com.coms309.Cynance.repository.ExpenseShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class ExpenseShareService {
    @Autowired
    private ExpenseShareRepository expenseShareRepository;

    public ExpenseShare addExpense(ExpenseShare expenseShare) {
        return expenseShareRepository.save(expenseShare);
    }

    public List<ExpenseShare> getExpensesByGroup(String groupName) {
        return expenseShareRepository.findByGroupName(groupName);
    }

    public boolean settleExpense(Long expenseId) {
        Optional<ExpenseShare> expense = expenseShareRepository.findById(expenseId);
        if (expense.isPresent()) {
            expenseShareRepository.delete(expense.get());
            return true;
        }
        return false;
    }
}
