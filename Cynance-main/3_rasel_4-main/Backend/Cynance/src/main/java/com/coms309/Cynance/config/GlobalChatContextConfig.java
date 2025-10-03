package com.coms309.Cynance.config;

import com.coms309.Cynance.repository.GlobalMessageRepository;
import com.coms309.Cynance.config.util.GlobalChatContextHolder;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalChatContextConfig {

    @Autowired
    private GlobalMessageRepository repo;

    @PostConstruct
    public void init() {
        GlobalChatContextHolder.setRepository(repo);
    }
}
