package com.coms309.Cynance.service;

import com.coms309.Cynance.model.*;
import com.coms309.Cynance.repository.BillPaymentStatusRepository;
import com.coms309.Cynance.repository.GroupBillRepository;
import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class GroupBillService {

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private GroupBillRepository billRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BillPaymentStatusRepository paymentRepo;

    public void createBillByGroupName(String groupName, BillRequest request) {
        Group group = groupRepo.findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));

        User payer = userRepo.findByUsername(request.getPaidByUsername())
                .orElseThrow(() -> new RuntimeException("Payer not found: " + request.getPaidByUsername()));

        // Create the bill
        GroupBill bill = new GroupBill();
        bill.setGroup(group);
        bill.setPaidBy(payer); // ✅ Use User object instead of setting ID
        bill.setTotalAmount(request.getTotalAmount());
        bill.setExpenseType(request.getExpenseType());
        bill.setCreatedAt(LocalDateTime.now());

        billRepo.save(bill);

        // Get user list and calculate split
        List<User> users = new ArrayList<>();
        for (String username : request.getSelectedUsernames()) {
            User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            users.add(user);
        }

        double splitAmount = request.getTotalAmount() / users.size();

        for (User user : users) {
            BillPaymentStatus payment = new BillPaymentStatus();
            payment.setBill(bill);
            payment.setUser(user);
            payment.setAmount(splitAmount);
            payment.setPaid(user.getId().equals(payer.getId())); // Mark payer as paid
            paymentRepo.save(payment);
        }

        // Filter out the payer from owedUsers
        Set<User> owedUsers = new HashSet<>();
        for (User user : users) {
            if (!user.getId().equals(payer.getId())) {
                owedUsers.add(user);
            }
        }

        // Auto-delete bill if no one owes anything
        if (owedUsers.isEmpty()) {
            List<BillPaymentStatus> allPayments = paymentRepo.findByBill(bill);
            paymentRepo.deleteAll(allPayments);
            billRepo.delete(bill);
            return;
        }

        // Save owed users
        bill.setOwedUsers(owedUsers);
        billRepo.save(bill);
    }


    public List<BillWithPaymentsDTO> getBillsWithPaymentsByGroupName(String groupName) {
        Group group = groupRepo.findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));

        List<GroupBill> bills = billRepo.findByGroup_IdOrderByCreatedAtDesc(group.getId());

        return bills.stream().map(bill -> {
            BillWithPaymentsDTO dto = new BillWithPaymentsDTO();
            dto.billId = bill.getId();
            dto.groupId = group.getId();

            // Assuming GroupBill has a User object reference for "paidBy"
            dto.paidByUsername = bill.getPaidBy().getUsername();  // Replace paidByUserId

            dto.totalAmount = bill.getTotalAmount();
            dto.expenseType = bill.getExpenseType();
            dto.createdAt = bill.getCreatedAt();

            List<BillWithPaymentsDTO.UserPayment> payments = paymentRepo.findByBillId(bill.getId()).stream()
                    .map(p -> new BillWithPaymentsDTO.UserPayment(p.getUser().getUsername(), p.isPaid()))
                    .toList();

            dto.payments = payments;
            return dto;
        }).toList();
    }

    public List<Map<String, Object>> getUserBillsInGroup(String groupName, Long userId) {
        Group group = groupRepo.findByName(groupName)
                .orElseThrow(() -> new RuntimeException("Group not found: " + groupName));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<GroupBill> bills = billRepo.findByGroup_IdOrderByCreatedAtDesc(group.getId());
        List<Map<String, Object>> results = new ArrayList<>();

        for (GroupBill bill : bills) {
            Optional<BillPaymentStatus> maybePayment = paymentRepo.findByBillAndUser(bill, user);
            if (maybePayment.isPresent()) {
                BillPaymentStatus payment = maybePayment.get();
                Map<String, Object> billMap = new HashMap<>();
                billMap.put("billId", bill.getId());
                billMap.put("paidByUsername", bill.getPaidBy().getUsername());
                billMap.put("totalAmount", bill.getTotalAmount());
                billMap.put("expenseType", bill.getExpenseType());
                billMap.put("createdAt", bill.getCreatedAt());
                billMap.put("amountOwed", payment.getAmount());
                billMap.put("isPaid", payment.isPaid());
                results.add(billMap);
            }
        }

        return results;
    }
}
