package com.coms309.Cynance;

import com.coms309.Cynance.model.*;
import com.coms309.Cynance.repository.*;
import com.coms309.Cynance.service.GroupBillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupBillServiceTest {

    @InjectMocks
    private GroupBillService groupBillService;

    @Mock
    private GroupRepository groupRepo;

    @Mock
    private GroupBillRepository billRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private BillPaymentStatusRepository paymentRepo;

    private Group group;
    private User payer;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        group = new Group();
        group.setId(1L);
        group.setName("TestGroup");

        payer = new User();
        payer.setId(1L);
        payer.setUsername("payer");

        user1 = new User();
        user1.setId(2L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(3L);
        user2.setUsername("user2");
    }

    @Test
    void testCreateBillByGroupName_Success() {
        BillRequest request = new BillRequest();
        request.setPaidByUsername("payer");
        request.setTotalAmount(90.0);
        request.setExpenseType("Dinner");
        request.setSelectedUsernames(List.of("payer", "user1", "user2"));

        when(groupRepo.findByName("TestGroup")).thenReturn(Optional.of(group));
        when(userRepo.findByUsername("payer")).thenReturn(Optional.of(payer));
        when(userRepo.findByUsername("user1")).thenReturn(Optional.of(user1));
        when(userRepo.findByUsername("user2")).thenReturn(Optional.of(user2));

        groupBillService.createBillByGroupName("TestGroup", request);

        ArgumentCaptor<BillPaymentStatus> captor = ArgumentCaptor.forClass(BillPaymentStatus.class);
        verify(paymentRepo, times(3)).save(captor.capture());

        List<BillPaymentStatus> saved = captor.getAllValues();
        for (BillPaymentStatus p : saved) {
            assertEquals(30.0, p.getAmount());  // 90 / 3
        }

        // Check payer is marked as paid
        assertTrue(saved.stream().anyMatch(p -> p.getUser().getUsername().equals("payer") && p.isPaid()));
    }

    @Test
    void testGetBillsWithPaymentsByGroupName_Success() {
        GroupBill bill = new GroupBill();
        bill.setId(1L);
        bill.setGroup(group);
        bill.setPaidBy(payer);
        bill.setTotalAmount(120.0);
        bill.setExpenseType("Lunch");
        bill.setCreatedAt(LocalDateTime.now());

        when(groupRepo.findByName("TestGroup")).thenReturn(Optional.of(group));
        when(billRepo.findByGroup_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(bill));

        BillPaymentStatus p1 = new BillPaymentStatus(user1,bill, 60.0, false);
        BillPaymentStatus p2 = new BillPaymentStatus(payer,bill, 60.0, true);
        when(paymentRepo.findByBillId(1L)).thenReturn(List.of(p1, p2));

        List<BillWithPaymentsDTO> result = groupBillService.getBillsWithPaymentsByGroupName("TestGroup");

        assertEquals(1, result.size());
        BillWithPaymentsDTO dto = result.get(0);
        assertEquals("Lunch", dto.expenseType);
        assertEquals("payer", dto.paidByUsername);
        assertEquals(2, dto.payments.size());
    }

    @Test
    void testGetUserBillsInGroup_Success() {
        GroupBill bill = new GroupBill();
        bill.setId(1L);
        bill.setGroup(group);
        bill.setPaidBy(payer);
        bill.setExpenseType("Snacks");
        bill.setTotalAmount(45.0);
        bill.setCreatedAt(LocalDateTime.now());

        BillPaymentStatus payment = new BillPaymentStatus();
        payment.setBill(bill);
        payment.setUser(user1);
        payment.setAmount(15.0);
        payment.setPaid(false);

        when(groupRepo.findByName("TestGroup")).thenReturn(Optional.of(group));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user1));
        when(billRepo.findByGroup_IdOrderByCreatedAtDesc(1L)).thenReturn(List.of(bill));
        when(paymentRepo.findByBillAndUser(bill, user1)).thenReturn(Optional.of(payment));

        List<Map<String, Object>> result = groupBillService.getUserBillsInGroup("TestGroup", 2L);

        assertEquals(1, result.size());
        Map<String, Object> map = result.get(0);
        assertEquals("Snacks", map.get("expenseType"));
        assertEquals(15.0, map.get("amountOwed"));
        assertFalse((Boolean) map.get("isPaid"));
    }
}

