package com.coms309.Cynance.controller;

import com.coms309.Cynance.service.BillPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
public class BillPaymentController {

    @Autowired
    private BillPaymentService billPaymentService;

    @PostMapping("/{billId}/settle/username/{username}")
    public ResponseEntity<String> settleUp(
            @PathVariable Long billId,
            @PathVariable String username
    ) {
        boolean allPaid = billPaymentService.settleUpByUsername(billId, username);
        if (allPaid) {
            return ResponseEntity.ok("All users have settled. Bill deleted.");
        }
        return ResponseEntity.ok("Payment marked as settled.");
    }

}

