package com.example.Lunara.user_service.service;

import com.example.Lunara.user_service.helperclass.EmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    @Autowired
    public MailService(JavaMailSender mailSender, EmailTemplateService emailTemplateService) {
        this.mailSender = mailSender;
        this.emailTemplateService = emailTemplateService;
    }

    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // HTML içeriği destekler

            mailSender.send(message);
            System.out.println("Mail sent successfully to: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
    public void sendHtmlMail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML içeriği gönder

            mailSender.send(message);
            System.out.println("HTML E-posta başarıyla gönderildi: " + to);
        } catch (MessagingException e) {
            throw new RuntimeException("E-posta gönderiminde hata: " + e.getMessage(), e);
        }
    }
    public void sendPasswordResetEmail(String to, String username, String resetUrl) {
        String subject = "Password Reset Request";
        String htmlContent = emailTemplateService.generateResetEmail(username, resetUrl);

        sendHtmlMail(to, subject, htmlContent);
    }
}
