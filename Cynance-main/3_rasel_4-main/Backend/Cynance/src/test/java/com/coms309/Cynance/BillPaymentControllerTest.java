package com.coms309.Cynance;

import com.coms309.Cynance.controller.BillPaymentController;
import com.coms309.Cynance.service.BillPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BillPaymentControllerTest {

    @Mock
    private BillPaymentService billPaymentService;

    @InjectMocks
    private BillPaymentController billPaymentController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSettleUp_AllPaid() {
        Long billId = 1L;
        String username = "testuser";

        when(billPaymentService.settleUpByUsername(billId, username)).thenReturn(true);

        ResponseEntity<String> response = billPaymentController.settleUp(billId, username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("All users have settled. Bill deleted.", response.getBody());
    }

    @Test
    public void testSettleUp_PartialPayment() {
        Long billId = 1L;
        String username = "testuser";

        when(billPaymentService.settleUpByUsername(billId, username)).thenReturn(false);

        ResponseEntity<String> response = billPaymentController.settleUp(billId, username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Payment marked as settled.", response.getBody());
    }

    @Test
    public void testSettleUp_ExceptionHandling() {
        Long billId = 999L;
        String username = "nonexistent";

        when(billPaymentService.settleUpByUsername(billId, username))
                .thenThrow(new RuntimeException("User not found with username: nonexistent"));

        try {
            billPaymentController.settleUp(billId, username);
        } catch (RuntimeException e) {
            assertEquals("User not found with username: nonexistent", e.getMessage());
        }
    }
}
