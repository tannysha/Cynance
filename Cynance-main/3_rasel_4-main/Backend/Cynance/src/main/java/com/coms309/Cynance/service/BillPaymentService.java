package com.coms309.Cynance.service;

import com.coms309.Cynance.model.BillPaymentStatus;
import com.coms309.Cynance.model.GroupBill;
import com.coms309.Cynance.model.User;
import com.coms309.Cynance.repository.BillPaymentStatusRepository;
import com.coms309.Cynance.repository.GroupBillRepository;
import com.coms309.Cynance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillPaymentService {

    @Autowired
    private BillPaymentStatusRepository billPaymentStatusRepository;

    @Autowired
    private GroupBillRepository groupBillRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean settleUp(Long billId, Long userId) {
        GroupBill bill = groupBillRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found with ID: " + billId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        BillPaymentStatus status = billPaymentStatusRepository
                .findByBillAndUser(bill, user)
                .orElseThrow(() -> new RuntimeException("No payment record found for this user and bill."));

        if (status.isPaid()) {
            throw new RuntimeException("This payment has already been settled.");
        }

        status.setPaid(true);
        billPaymentStatusRepository.save(status);

        // Check if all users have paid
        List<BillPaymentStatus> allStatuses = billPaymentStatusRepository.findByBill(bill);
        boolean allPaid = allStatuses.stream().allMatch(BillPaymentStatus::isPaid);

        if (allPaid) {
            billPaymentStatusRepository.deleteAll(allStatuses);
            groupBillRepository.delete(bill);
            return true;
        }

        return false;
    }
    public boolean settleUpByUsername(Long billId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        return settleUp(billId, user.getId());
    }


}
