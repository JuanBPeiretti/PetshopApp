package com.petshop.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogNotificationService implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger("notificationLogger");

    @Override
    public void notify(String userEmail, String message) {
        LOG.info("[SIMULATED NOTIFICATION] to {} | {}", userEmail, message);
    }
}
