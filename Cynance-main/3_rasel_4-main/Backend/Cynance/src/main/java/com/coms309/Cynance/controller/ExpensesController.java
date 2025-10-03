package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.CategorySpendingDTO;
import com.coms309.Cynance.model.Expenses;
import com.coms309.Cynance.model.MaxCategoryBillDTO;
import com.coms309.Cynance.service.ExpenseService;
import com.coms309.Cynance.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses/")
public class ExpensesController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("add/{username}")
    public ResponseEntity<Map<String, String>> addExpense(
            @PathVariable("username") String username,
            @RequestBody Expenses expense) {

        Map<String, String> response = new HashMap<>();

        boolean added = expenseService.addExpense(username, expense);
        if (added) {
            String msg = " New expense added: " + expense.getTitle() + " - $" + expense.getPrice();
            notificationService.notifyUser(username, "EXPENSE", msg);
            response.put("message", "Expense added successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "Expense already exists or user not found.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("list/{username}")
    public ResponseEntity<?> getExpenses(@PathVariable("username") String username) {
        List<Expenses> expenses = expenseService.getExpenses(username);

        if (expenses.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No expenses found for this user.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(expenses);
    }

    @GetMapping("get/{username}/{title}")
    public ResponseEntity<?> getExpense(
            @PathVariable("username") String username,
            @PathVariable("title") String title) {

        Optional<Expenses> expense = expenseService.getExpense(username, title);

        if (expense.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Expense not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(expense.get());
    }

    @PutMapping("update/{username}/{title}")
    public ResponseEntity<Map<String, String>> updateExpense(
            @PathVariable("username") String username,
            @PathVariable("title") String title,
            @RequestBody Expenses updatedExpense) {

        Map<String, String> response = new HashMap<>();

        boolean updated = expenseService.updateExpense(username, title, updatedExpense);
        if (updated) {
            String msg = "Expense updated: " + updatedExpense.getTitle() + " is now $" + updatedExpense.getPrice();
            notificationService.notifyUser(username, "EXPENSE", msg);
            response.put("message", "Expense updated successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "Expense not found or update failed.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @DeleteMapping("delete/{username}/{title}")
    public ResponseEntity<Map<String, String>> deleteExpense(
            @PathVariable("username") String username,
            @PathVariable("title") String title) {

        Map<String, String> response = new HashMap<>();

        boolean deleted = expenseService.removeExpense(username, title);
        if (deleted) {
            String msg = "Expense deleted: " + title;
            notificationService.notifyUser(username, "EXPENSE", msg);
            response.put("message", "Expense deleted successfully");
            return ResponseEntity.ok(response);
        }

        response.put("error", "Expense not found or could not be deleted.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @GetMapping("analytics/{username}")
    public ResponseEntity<?> getCategoryAnalytics(@PathVariable("username") String username) {
        List<CategorySpendingDTO> data = expenseService.getSpendingByCategory(username);

        if (data.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No expenses found for analytics.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(data);
    }
    @GetMapping("analytics/max-full/{username}")
    public ResponseEntity<?> getMaxCategoryAndTopBill(@PathVariable("username") String username) {
        MaxCategoryBillDTO result = expenseService.getMaxCategoryAndTopExpense(username);

        if (result.getCategory().equals("N/A")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No expenses found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(result);
    }

}
