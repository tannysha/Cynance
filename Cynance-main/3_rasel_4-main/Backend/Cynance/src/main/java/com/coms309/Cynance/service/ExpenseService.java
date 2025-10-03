package com.coms309.Cynance.service;

import com.coms309.Cynance.model.*;
import com.coms309.Cynance.repository.ExpensesRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ExpenseService {

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private UserRepository userRepository;


    public boolean addExpense(String username, Expenses expense) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        expense.setUser(user);
        expensesRepository.save(expense);
        return true;
    }

    public boolean removeExpense(String username, String title) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        Optional<Expenses> expense = expensesRepository.findByUserAndTitle(user, title);
        if (expense.isEmpty()) return false;

        expensesRepository.delete(expense.get());
        return true;
    }


    public List<Expenses> getExpenses(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return List.of(); // Return an empty list instead of null
        return expensesRepository.findByUser(user);
    }


    public Optional<Expenses> getExpense(String username, String title) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return Optional.empty();
        return expensesRepository.findByUserAndTitle(user, title);
    }


    public boolean updateExpense(String username, String title, Expenses updatedExpense) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) return false;

        Optional<Expenses> existingExpense = expensesRepository.findByUserAndTitle(user, title);
        if (existingExpense.isEmpty()) return false; // Expense not found

        Expenses expense = existingExpense.get();
        expense.setDate(updatedExpense.getDate());
        expense.setDescription(updatedExpense.getDescription());
        expense.setPrice(updatedExpense.getPrice());

        expensesRepository.save(expense);
        return true;
    }
    public List<CategorySpendingDTO> getSpendingByCategory(String username) {
        List<Expenses> expenses = getExpenses(username);

        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expenses e : expenses) {
            String category = e.getTitle();
            double amount = 0.0;
            try {
                amount = Double.parseDouble(e.getPrice());
            } catch (NumberFormatException ex) {
                continue;
            }

            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);
        }

        List<CategorySpendingDTO> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            result.add(new CategorySpendingDTO(entry.getKey(), entry.getValue()));
        }

        return result;
    }

    public MaxCategoryBillDTO getMaxCategoryAndTopExpense(String username) {
        List<Expenses> expenses = getExpenses(username);
        if (expenses.isEmpty()) {
            return new MaxCategoryBillDTO("N/A", 0.0, null);
        }

        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Expenses> maxBillPerCategory = new HashMap<>();

        for (Expenses e : expenses) {
            String category = e.getTitle();
            double amount;
            try {
                amount = Double.parseDouble(e.getPrice());
            } catch (NumberFormatException ex) {
                continue;
            }

            // Sum category totals
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + amount);

            // Track max bill per category
            if (!maxBillPerCategory.containsKey(category) ||
                    Double.parseDouble(maxBillPerCategory.get(category).getPrice()) < amount) {
                maxBillPerCategory.put(category, e);
            }
        }

        // Find the max category overall
        String maxCategory = null;
        double maxTotal = 0.0;

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            if (entry.getValue() > maxTotal) {
                maxCategory = entry.getKey();
                maxTotal = entry.getValue();
            }
        }

        Expenses topExpense = maxBillPerCategory.get(maxCategory);
        ExpenseSummary summary = new ExpenseSummary(
                topExpense.getTitle(),
                topExpense.getDate(),
                topExpense.getDescription(),
                Double.parseDouble(topExpense.getPrice())
        );

        return new MaxCategoryBillDTO(maxCategory, maxTotal, summary);
    }


}
