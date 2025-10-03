package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.GlobalMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GlobalMessageRepository extends JpaRepository<GlobalMessage, Long> {
    List<GlobalMessage> findTop50ByOrderBySentDesc(); // Optional for recent chat loading
}
