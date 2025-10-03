package com.coms309.Cynance.repository;

import com.coms309.Cynance.model.Message;
import com.coms309.Cynance.model.Reaction;
import com.coms309.Cynance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserAndMessageAndEmoji(User user, Message message, String emoji);
}

