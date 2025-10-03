package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.ExpenseShare;
import com.coms309.Cynance.service.ExpenseShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseShareController {
    @Autowired
    private ExpenseShareService expenseShareService;

    @PostMapping("/add")
    public ResponseEntity<ExpenseShare> addExpense(@RequestBody ExpenseShare expenseShare) {
        return ResponseEntity.ok(expenseShareService.addExpense(expenseShare));
    }

    @GetMapping("/group/{groupName}")
    public ResponseEntity<List<ExpenseShare>> getGroupExpenses(@PathVariable String groupName) {
        return ResponseEntity.ok(expenseShareService.getExpensesByGroup(groupName));
    }

    @PutMapping("/settle/{expenseId}")
    public ResponseEntity<String> settleExpense(@PathVariable Long expenseId) {
        boolean settled = expenseShareService.settleExpense(expenseId);
        return settled ? ResponseEntity.ok("Expense settled successfully.") : ResponseEntity.badRequest().body("Expense not found.");
    }
}
