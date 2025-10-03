package com.coms309.Cynance.model;

import java.util.List;

public class SearchResultDTO {
    private List<Expenses> expenses;
    private List<Income> incomes;
    private List<Subscription> subscriptions;

    // Getters and setters
    public List<Expenses> getExpenses() {
        return expenses;
    }
    public void setExpenses(List<Expenses> expenses) {
        this.expenses = expenses;
    }
    public List<Income> getIncomes() {
        return incomes;
    }
    public void setIncomes(List<Income> incomes) {
        this.incomes = incomes;
    }
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }
    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
