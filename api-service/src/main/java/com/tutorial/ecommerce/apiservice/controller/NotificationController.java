package com.tutorial.ecommerce.apiservice.controller;

import com.tutorial.ecommerce.apiservice.dto.NotificationRequest;
import com.tutorial.ecommerce.apiservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/notifications")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest notificationRequest) {
        notificationService.sendNotification(notificationRequest);
        return new ResponseEntity<>("Notification sent successfully", HttpStatus.OK);
    }
}
