package com.tutorial.ecommerce.apiservice.client;

import com.tutorial.ecommerce.apiservice.dto.NotificationRequest;
import com.tutorial.ecommerce.model.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
// sample client not used in this project
public class NotificationClient {

    private final RestTemplate restTemplate;

    public NotificationClient() {
        this.restTemplate = new RestTemplate();
    }

    public void sendOrderPlacedNotification(NotificationRequest notificationRequest) {
        String url = "http://notification-service/sendOrderNotification";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<NotificationRequest> requestEntity = new HttpEntity<>(notificationRequest, headers);

        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            // Handle error or retry logic here
            throw new RuntimeException("Failed to send notification");
        }
    }
}
