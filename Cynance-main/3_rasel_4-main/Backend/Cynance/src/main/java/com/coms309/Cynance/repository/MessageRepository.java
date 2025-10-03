package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.GroupChat;
import com.coms309.Cynance.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByGroupIdOrderByTimestampAsc(Long groupId);
}
