package com.coms309.Cynance;

import com.coms309.Cynance.controller.GroupBillController;
import com.coms309.Cynance.model.BillRequest;
import com.coms309.Cynance.model.BillWithPaymentsDTO;
import com.coms309.Cynance.model.Group;
import com.coms309.Cynance.model.UserDTO;
import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.service.BillPaymentService;
import com.coms309.Cynance.service.GroupBillService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupBillControllerTest {

    @Mock
    private GroupBillService billService;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private BillPaymentService billPaymentService;

    @InjectMocks
    private GroupBillController groupBillController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBill() {
        String groupName = "TestGroup";
        BillRequest request = new BillRequest();

        ResponseEntity<?> response = groupBillController.createBill(groupName, request);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Bill split successfully", ((java.util.Map<?, ?>) response.getBody()).get("message"));
    }

    @Test
    void testGetBillsWithPayments() {
        String groupName = "TestGroup";
        when(billService.getBillsWithPaymentsByGroupName(groupName)).thenReturn(List.of(new BillWithPaymentsDTO()));

        ResponseEntity<List<BillWithPaymentsDTO>> response = groupBillController.getBillsWithPayments(groupName);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetUsersByGroupName() {
        String groupName = "TestGroup";
        Group group = new Group();
        group.setName(groupName);
        group.setMembers(new HashSet<>());

        when(groupRepository.findByName(groupName)).thenReturn(Optional.of(group));

        ResponseEntity<List<UserDTO>> response = groupBillController.getUsersByGroupName(groupName);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }



    @Test
    void testSettleUp_Failure() {
        String username = "john";
        Long billId = 1L;

        doThrow(new RuntimeException("Some error")).when(billPaymentService).settleUpByUsername(billId, username);

        ResponseEntity<?> response = groupBillController.settleUp(billId, username);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Some error", ((java.util.Map<?, ?>) response.getBody()).get("error"));
    }
}
