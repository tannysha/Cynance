package com.coms309.Cynance.model;

import java.time.LocalDateTime;
import java.util.List;

public class BillWithPaymentsDTO {

    public static class UserPayment {
        public String username;
        public boolean isPaid;

        public UserPayment(String username, boolean isPaid) {
            this.username = username;
            this.isPaid = isPaid;
        }
    }

    public Long billId;
    public Long groupId;
    public String paidByUsername;
    public Double totalAmount;
    public String expenseType;
    public LocalDateTime createdAt;
    public List<UserPayment> payments;

    public BillWithPaymentsDTO() {}
}
