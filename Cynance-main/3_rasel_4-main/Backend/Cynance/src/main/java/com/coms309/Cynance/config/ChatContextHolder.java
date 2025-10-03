package com.coms309.Cynance.config;

import com.coms309.Cynance.repository.GroupRepository;
import com.coms309.Cynance.repository.MessageRepository;
import com.coms309.Cynance.service.NotificationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ChatContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        context = ctx;
    }

    public static MessageRepository getMessageRepository() {
        return context.getBean(MessageRepository.class);
    }

    public static GroupRepository getGroupRepository() {
        return context.getBean(GroupRepository.class);
    }

    public static NotificationService getNotificationService() {
        return context.getBean(NotificationService.class);
    }
}
