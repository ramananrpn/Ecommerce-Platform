package com.tutorial.ecommerce.apiservice.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private String recipientEmail;
    private String subject;
    private String message;
}
