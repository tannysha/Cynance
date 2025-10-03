package com.coms309.Cynance.service;

import com.coms309.Cynance.model.*;
import com.coms309.Cynance.repository.IncomeRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Income> getIncomeByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return incomeRepository.findByUser(user);
    }

    public Income addIncome(String username, Income income) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        income.setUser(user); // Associate income with user
        return incomeRepository.save(income); // Save new income record
    }

    public Income updateIncome(String username, String source, Income updatedIncome) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Income> userIncomeList = incomeRepository.findByUserAndSource(user, source);
        if (userIncomeList.isEmpty()) {
            throw new RuntimeException("No income record found for user: " + username + " with source: " + source);
        }

        // Assuming one record per source per user
        Income existingIncome = userIncomeList.get(0);

        existingIncome.setAmount(updatedIncome.getAmount());
        existingIncome.setFrequency(updatedIncome.getFrequency());

        return incomeRepository.save(existingIncome);
    }

    public void deleteIncomeBySource(String username, String source) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Income> userIncomeList = incomeRepository.findByUserAndSource(user, source);

        if (userIncomeList.isEmpty()) {
            throw new RuntimeException("No income record found for user: " + username + " with source: " + source);
        }

        incomeRepository.delete(userIncomeList.get(0)); // Delete first matching record
    }

    public void deleteAllIncome(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Income> userIncomeList = incomeRepository.findByUser(user);

        if (userIncomeList.isEmpty()) {
            throw new RuntimeException("No income records found for user: " + username);
        }

        incomeRepository.deleteAll(userIncomeList);
    }
    public MaxSourceIncomeDTO getMaxIncomeSourceAnalytics(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Income> incomeList = incomeRepository.findByUser(user);
        if (incomeList.isEmpty()) {
            return new MaxSourceIncomeDTO("N/A", 0.0, null);
        }

        Map<String, Double> sourceTotals = new HashMap<>();
        Map<String, Income> maxIncomePerSource = new HashMap<>();

        for (Income i : incomeList) {
            String source = i.getSource();
            double amount = i.getAmount();

            sourceTotals.put(source, sourceTotals.getOrDefault(source, 0.0) + amount);

            if (!maxIncomePerSource.containsKey(source) || maxIncomePerSource.get(source).getAmount() < amount) {
                maxIncomePerSource.put(source, i);
            }
        }

        String maxSource = null;
        double maxTotal = 0.0;

        for (Map.Entry<String, Double> entry : sourceTotals.entrySet()) {
            if (entry.getValue() > maxTotal) {
                maxSource = entry.getKey();
                maxTotal = entry.getValue();
            }
        }

        Income topIncome = maxIncomePerSource.get(maxSource);
        IncomeSummary summary = new IncomeSummary(topIncome.getSource(), topIncome.getAmount(), topIncome.getFrequency());

        return new MaxSourceIncomeDTO(maxSource, maxTotal, summary);
    }
    public List<SourceIncomeDTO> getIncomeBreakdownBySource(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        List<Income> incomeList = incomeRepository.findByUser(user);

        Map<String, Double> totals = new HashMap<>();
        for (Income i : incomeList) {
            String source = i.getSource();
            double amount = i.getAmount();
            totals.put(source, totals.getOrDefault(source, 0.0) + amount);
        }

        List<SourceIncomeDTO> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            result.add(new SourceIncomeDTO(entry.getKey(), entry.getValue()));
        }

        return result;
    }

}
