package com.khangdjnh.edu_app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    JavaMailSender mailSender;

    public void sendConfirmationEmail(String to, String token) {
        String subject = "Xác nhận tài khoản Education System";
        String confirmationLink = "http://localhost:5173/confirm-email?token=" + token;
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
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
