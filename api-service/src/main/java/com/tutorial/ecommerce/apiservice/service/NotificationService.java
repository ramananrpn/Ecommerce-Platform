package com.tutorial.ecommerce.apiservice.service;

import com.tutorial.ecommerce.apiservice.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Value("${spring.mail.from}")
    private String fromEmail;

    private final JavaMailSender emailSender;

    public NotificationService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendNotification(NotificationRequest notificationRequest) {
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(emailSender.createMimeMessage(), true);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(notificationRequest.getRecipientEmail());
            messageHelper.setSubject(notificationRequest.getSubject());
            messageHelper.setText(notificationRequest.getMessage(), true);
            emailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            log.error("Failed to send email notification", e);
        }
    }
}
