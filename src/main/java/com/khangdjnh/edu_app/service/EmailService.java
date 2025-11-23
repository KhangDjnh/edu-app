package com.khangdjnh.edu_app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        String subject = "Xác nhận tài khoản Education System";
        String confirmationLink = "http://localhost:3000/confirmEmail?token=" + token;
        String content = "<p>Chào bạn,</p>"
                + "<p>Vui lòng click vào link dưới đây để xác nhận đăng ký tài khoản:</p>"
                + "<p><a href=\"" + confirmationLink + "\">Xác nhận tài khoản</a></p>"
                + "<p>Link sẽ hết hạn trong 15 phút.</p>";

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Send email to {}", to);
        } catch (MessagingException e) {
            log.error("Email sending failed: {}", e.getMessage(), e);

            // In thêm nguyên nhân gốc (nested exception)
            Exception nextEx = e.getNextException();
            if (nextEx != null) {
                log.error("Root cause: {}", nextEx.getMessage(), nextEx);
            }

            throw new RuntimeException("Failed to send email", e);
        }
    }
}
