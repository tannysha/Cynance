package com.coms309.Cynance;

import com.coms309.Cynance.controller.IncomeController;
import com.coms309.Cynance.model.Income;
import com.coms309.Cynance.model.MaxSourceIncomeDTO;
import com.coms309.Cynance.model.SourceIncomeDTO;
import com.coms309.Cynance.service.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IncomeControllerTest {

    @Mock
    private IncomeService incomeService;

    @InjectMocks
    private IncomeController incomeController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetIncomeHistory() {
        String username = "john";
        Income income = new Income();
        income.setAmount(1000.0);
        income.setSource("Job");

        when(incomeService.getIncomeByUsername(username)).thenReturn(List.of(income));

        List<Income> result = incomeController.getIncomeHistory(username);
        assertEquals(1, result.size());
        assertEquals("Job", result.get(0).getSource());
    }

    @Test
    void testAddIncome() {
        String username = "john";
        Income income = new Income();
        income.setSource("Job");
        income.setAmount(2000.0);

        when(incomeService.addIncome(username, income)).thenReturn(income);

        Income result = incomeController.addIncome(username, income);
        assertEquals("Job", result.getSource());
    }

    @Test
    void testUpdateIncome() {
        String username = "john";
        String source = "Freelance";
        Income updated = new Income();
        updated.setAmount(500.0);

        when(incomeService.updateIncome(username, source, updated)).thenReturn(updated);

        Income result = incomeController.updateIncome(username, source, updated);
        assertEquals(500.0, result.getAmount());
    }

    @Test
    void testDeleteIncomeBySource() {
        String username = "john";
        String source = "Bonus";

        doNothing().when(incomeService).deleteIncomeBySource(username, source);

        String result = incomeController.deleteIncomeBySource(username, source);
        assertTrue(result.contains("deleted successfully"));
    }

    @Test
    void testDeleteAllIncome() {
        String username = "john";

        doNothing().when(incomeService).deleteAllIncome(username);

        String result = incomeController.deleteAllIncome(username);
        assertTrue(result.contains("deleted successfully"));
    }

    @Test
    void testGetMaxIncomeAnalytics_Found() {
        String username = "john";
        MaxSourceIncomeDTO dto = new MaxSourceIncomeDTO("Job", 3000.0, null);

        when(incomeService.getMaxIncomeSourceAnalytics(username)).thenReturn(dto);

        ResponseEntity<?> response = incomeController.getMaxIncomeAnalytics(username);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(dto, response.getBody());
    }

    @Test
    void testGetMaxIncomeAnalytics_NotFound() {
        String username = "john";
        MaxSourceIncomeDTO dto = new MaxSourceIncomeDTO("N/A", 0.0, null);

        when(incomeService.getMaxIncomeSourceAnalytics(username)).thenReturn(dto);

        ResponseEntity<?> response = incomeController.getMaxIncomeAnalytics(username);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetIncomeBreakdown_Found() {
        String username = "john";
        List<SourceIncomeDTO> breakdown = List.of(new SourceIncomeDTO("Job", 3000.0));

        when(incomeService.getIncomeBreakdownBySource(username)).thenReturn(breakdown);

        ResponseEntity<?> response = incomeController.getIncomeBreakdown(username);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(breakdown, response.getBody());
    }

    @Test
    void testGetIncomeBreakdown_Empty() {
        String username = "john";

        when(incomeService.getIncomeBreakdownBySource(username)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = incomeController.getIncomeBreakdown(username);
        assertEquals(404, response.getStatusCodeValue());
    }
}

