package com.example.fsneaker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSerder;

    public void sendInvoiceEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSerder.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        try {
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true); //true: sử dụng html

            mailSerder.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }
}
