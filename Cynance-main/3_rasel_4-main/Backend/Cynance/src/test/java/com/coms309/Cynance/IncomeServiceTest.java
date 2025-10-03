package com.coms309.Cynance;

import com.coms309.Cynance.model.*;
import com.coms309.Cynance.repository.IncomeRepository;
import com.coms309.Cynance.repository.UserRepository;
import com.coms309.Cynance.service.IncomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IncomeServiceTest {

    @InjectMocks
    private IncomeService incomeService;

    @Mock
    private IncomeRepository incomeRepository;

    @Mock
    private UserRepository userRepository;

    private User mockUser;
    private Income mockIncome;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockIncome = new Income(5000.0, "Job", "Monthly", List.of(5000.0), mockUser);
        mockIncome.setId(1L);
    }

    @Test
    void testAddIncome() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(incomeRepository.save(any(Income.class))).thenReturn(mockIncome);

        Income saved = incomeService.addIncome("testuser", mockIncome);

        assertEquals("Job", saved.getSource());
        verify(incomeRepository, times(1)).save(mockIncome);
    }

    @Test
    void testGetIncomeByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(incomeRepository.findByUser(mockUser)).thenReturn(List.of(mockIncome));

        List<Income> result = incomeService.getIncomeByUsername("testuser");

        assertEquals(1, result.size());
        assertEquals("Job", result.get(0).getSource());
    }

    @Test
    void testUpdateIncome() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(incomeRepository.findByUserAndSource(mockUser, "Job")).thenReturn(List.of(mockIncome));
        when(incomeRepository.save(any(Income.class))).thenReturn(mockIncome);

        Income updated = new Income(6000.0, "Job", "Biweekly", List.of(6000.0), mockUser);
        Income result = incomeService.updateIncome("testuser", "Job", updated);

        assertEquals(6000.0, result.getAmount());
        assertEquals("Biweekly", result.getFrequency());
    }

    @Test
    void testDeleteIncomeBySource() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(incomeRepository.findByUserAndSource(mockUser, "Job")).thenReturn(List.of(mockIncome));

        incomeService.deleteIncomeBySource("testuser", "Job");

        verify(incomeRepository).delete(mockIncome);
    }

    @Test
    void testDeleteAllIncome() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(incomeRepository.findByUser(mockUser)).thenReturn(List.of(mockIncome));

        incomeService.deleteAllIncome("testuser");

        verify(incomeRepository).deleteAll(List.of(mockIncome));
    }


    @Test
    void testGetIncomeBreakdownBySource() {
        Income secondIncome = new Income(2000.0, "Freelance", "One-Time", List.of(2000.0), mockUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(incomeRepository.findByUser(mockUser)).thenReturn(List.of(mockIncome, secondIncome));

        List<SourceIncomeDTO> breakdown = incomeService.getIncomeBreakdownBySource("testuser");

        assertEquals(2, breakdown.size());
        assertTrue(breakdown.stream().anyMatch(i -> i.getSource().equals("Job")));
        assertTrue(breakdown.stream().anyMatch(i -> i.getSource().equals("Freelance")));
    }

    @Test
    void testUserNotFoundThrowsException() {
        when(userRepository.findByUsername("invalid")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> incomeService.getIncomeByUsername("invalid"));
        assertTrue(ex.getMessage().contains("User not found"));
    }
}

