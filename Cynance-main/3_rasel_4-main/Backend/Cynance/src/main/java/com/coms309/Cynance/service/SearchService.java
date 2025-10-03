package com.coms309.Cynance.service;

import com.coms309.Cynance.model.SearchResultDTO;
import com.coms309.Cynance.repository.ExpensesRepository;
import com.coms309.Cynance.repository.IncomeRepository;
import com.coms309.Cynance.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

    @Autowired
    private ExpensesRepository expenseRepo;

    @Autowired
    private IncomeRepository incomeRepo;

    @Autowired
    private SubscriptionRepository subscriptionRepo;

    public SearchResultDTO search(String query, String username) {
        SearchResultDTO result = new SearchResultDTO();

        result.setExpenses(expenseRepo.searchByKeyword(query, username));
        result.setIncomes(incomeRepo.searchByKeyword(query, username));
        result.setSubscriptions(subscriptionRepo.searchByKeyword(query, username));

        return result;
    }
}

