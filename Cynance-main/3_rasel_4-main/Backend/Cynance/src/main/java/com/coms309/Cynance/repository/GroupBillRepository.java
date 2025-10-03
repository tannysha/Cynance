package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.GroupBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupBillRepository extends JpaRepository<GroupBill, Long> {
    List<GroupBill> findByGroup_IdOrderByCreatedAtDesc(Long groupId);
}
