package com.example.bucketlist.service;

import com.example.bucketlist.exception.DuplicateMailSignupException;
import com.example.bucketlist.exception.DuplicateMailVerificationException;
import com.example.bucketlist.exception.ErrorCode;
import com.example.bucketlist.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final MemberRepository memberRepository;

    @Autowired
    public MailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine, RedisTemplate<String, Integer> redisTemplate, MemberRepository memberRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.redisTemplate = redisTemplate;
        this.memberRepository = memberRepository;
    }

    public void sendMailVerificationCode(String email) throws MessagingException {

        if (memberRepository.existsByEmail(email))
            throw new DuplicateMailSignupException(ErrorCode.DUPLICATE_MAIL_SIGNUP);

        if (redisTemplate.hasKey(email))
            throw new DuplicateMailVerificationException(ErrorCode.DUPLICATE_MAIL_VERIFICATION);

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

        ValueOperations<String, Integer> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(email, code);
        redisTemplate.expire(email, 5, TimeUnit.MINUTES);

    }

    public boolean checkMailVerificationCode(String email, int inputCode) {

        Boolean isEmailExists = redisTemplate.hasKey(email);
        if (isEmailExists) {
            int emailCode = redisTemplate.opsForValue().get(email).intValue();
            if (emailCode == inputCode) {
                redisTemplate.delete(email);
                return true;
            }
        }

        return false;
    }
}
