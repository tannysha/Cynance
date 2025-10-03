package com.coms309.Cynance.controller;

import com.coms309.Cynance.model.BillRequest;
import com.coms309.Cynance.model.BillWithPaymentsDTO;
import com.coms309.Cynance.model.Group;
import com.coms309.Cynance.model.UserDTO;
import com.coms309.Cynance.repository.GroupBillRepository;
import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.service.BillPaymentService;
import com.coms309.Cynance.service.GroupBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
public class GroupBillController {

    @Autowired
    private GroupBillService billService;

    @Autowired
    private GroupBillRepository groupBillRepository;

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private BillPaymentService billPaymentService;



    @PostMapping("/name/{groupName}/bill")
    public ResponseEntity<Map<String, String>> createBill(@PathVariable String groupName, @RequestBody BillRequest request) {
        billService.createBillByGroupName(groupName, request);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Bill split successfully");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/name/{groupName}/bills")
    public ResponseEntity<List<BillWithPaymentsDTO>> getBillsWithPayments(@PathVariable String groupName) {
        List<BillWithPaymentsDTO> response = billService.getBillsWithPaymentsByGroupName(groupName);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/group-bill/name/{groupName}/users")
    public ResponseEntity<List<UserDTO>> getUsersByGroupName(@PathVariable String groupName) {
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        List<UserDTO> users = group.getMembers().stream()
                .map(UserDTO::new)
                .toList();

        return ResponseEntity.ok(users);
    }
    @PutMapping("/bill/{billId}/settle-up/username/{username}")
    public ResponseEntity<?> settleUp(@PathVariable Long billId, @PathVariable String username) {
        try {
            billPaymentService.settleUpByUsername(billId, username);
            return ResponseEntity.ok(Map.of("message", "Payment settled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
