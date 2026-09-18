package com.banking.notificationservice;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationServiceController {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceController.class);

    @PostMapping("/send")
    @Observed(name = "notification.send")
    public Map<String, Object> send(@RequestBody Map<String, Object> req) {
        log.info("Sending notification channel={} to={}", req.get("channel"), req.get("to"));
        return Map.of("status", "SENT");
    }
}