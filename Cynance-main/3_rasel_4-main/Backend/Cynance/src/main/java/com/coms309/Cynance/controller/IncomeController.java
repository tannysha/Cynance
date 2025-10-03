package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.Income;
import com.coms309.Cynance.model.MaxSourceIncomeDTO;
import com.coms309.Cynance.model.SourceIncomeDTO;
import com.coms309.Cynance.service.IncomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/income")
public class IncomeController {

    @Autowired
    private IncomeService incomeService;

    // Get all income records for a user
    @GetMapping("history/{username}")
    public List<Income> getIncomeHistory(@PathVariable String username) {
        return incomeService.getIncomeByUsername(username);
    }

    // Add a new income record
    @PostMapping("add/{username}")
    public Income addIncome(@PathVariable String username, @RequestBody Income income) {
        return incomeService.addIncome(username, income);
    }

    // Update an income record based on source
    @PutMapping("edit/{username}/{source}")
    public Income updateIncome(
            @PathVariable String username,
            @PathVariable String source,
            @RequestBody Income updatedIncome) {
        return incomeService.updateIncome(username, source, updatedIncome);
    }

    // Delete a specific income record by source
    @DeleteMapping("delete/{username}/{source}")
    public String deleteIncomeBySource(@PathVariable String username, @PathVariable String source) {
        incomeService.deleteIncomeBySource(username, source);
        return "Income record with source '" + source + "' deleted successfully for user " + username;
    }

    // Delete all income records for a user
    @DeleteMapping("delete/{username}")
    public String deleteAllIncome(@PathVariable String username) {
        incomeService.deleteAllIncome(username);
        return "All income records for " + username + " deleted successfully";
    }
    @GetMapping("analytics/max-full/{username}")
    public ResponseEntity<?> getMaxIncomeAnalytics(@PathVariable String username) {
        MaxSourceIncomeDTO result = incomeService.getMaxIncomeSourceAnalytics(username);

        if (result.getSource().equals("N/A")) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No income records found.");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(result);
    }
    @GetMapping("analytics/source-breakdown/{username}")
    public ResponseEntity<?> getIncomeBreakdown(@PathVariable String username) {
        List<SourceIncomeDTO> data = incomeService.getIncomeBreakdownBySource(username);

        if (data.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No income records found.");
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(data);
    }


}
