package org.cs304.backend.service.impl;

import jakarta.annotation.Resource;
import org.cs304.backend.exception.ServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author zyp
 * @date 2024/4/22 14:08
 * @description
 **/
@Service
public class EmailService {

    @Resource
    JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderAddress;

    public void sendEmail(String to, String title, String content, LocalDateTime dateTime) {
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
        long delay = ChronoUnit.MILLIS.between(ZonedDateTime.now(), zonedDateTime);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> sendEmail_(to, title, content), delay, TimeUnit.MILLISECONDS);
    }

    private void sendEmail_(String to, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setSubject(title);
        message.setText(content);
        message.setTo(to);
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new ServiceException("Email sending failed", e.getMessage());
        }
    }

//    /**
//     * 发送邮箱验证码操作
//     *
//     * @param to 邮箱
//     * @param title 邮件标题
//     * @param content 邮件内容
//     * @param dateTime 发送时间
//     *
//     */
//    public void sendEmail(String to, String title, String content, LocalDateTime dateTime) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(senderAddress);
//        Date date = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
//        System.out.println(date);
//        message.setSentDate(date);
//        message.setSubject(title);
//        message.setText(content);
//        message.setTo(to);
//        try {
//            mailSender.send(message);
//        } catch (Exception e) {
//            throw new ServiceException("Email sending failed");
//        }
//    }
    public void sendImmediateEmail(String to, String title, String content) {
        sendEmail(to,title,content,LocalDateTime.now());
    }


}
