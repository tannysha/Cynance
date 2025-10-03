package com.coms309.Cynance.config.util;

import com.coms309.Cynance.repository.GlobalMessageRepository;
import org.springframework.stereotype.Component;

@Component
public class GlobalChatContextHolder {

    private static GlobalMessageRepository globalMessageRepository;

    public static GlobalMessageRepository getRepository() {
        return globalMessageRepository;
    }

    public static void setRepository(GlobalMessageRepository repo) {
        globalMessageRepository = repo;
    }
}
