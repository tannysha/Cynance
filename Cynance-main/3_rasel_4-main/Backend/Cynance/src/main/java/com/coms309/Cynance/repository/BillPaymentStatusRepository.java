package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.BillPaymentStatus;
import com.coms309.Cynance.model.GroupBill;
import com.coms309.Cynance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BillPaymentStatusRepository extends JpaRepository<BillPaymentStatus, Long> {
    Optional<BillPaymentStatus> findByBillIdAndUserId(Long billId, Long userId);
    List<BillPaymentStatus> findByBillId(Long billId);
    Optional<BillPaymentStatus> findByBillAndUser(GroupBill bill, User user);
    List<BillPaymentStatus> findByBill(GroupBill bill);



}

