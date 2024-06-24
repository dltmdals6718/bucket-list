package com.example.bucketlist.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public MailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMailVerificationCode(String email) throws MessagingException {

        Integer code = new Random().nextInt(10000, 100000);

        Context context = new Context();
        context.setVariable("code", code);
        String html = templateEngine.process("mailVerification", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
        messageHelper.setTo(email);
        messageHelper.setSubject("우리들의 버킷 리스트 인증 번호");
        messageHelper.setText(html, true);

        mailSender.send(mimeMessage);
    }
}
