package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService实现类
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 类中通过@Autowired注解注入了一个JavaMailSender对象mailSender，用于发送邮件。
     * 重写了EmailService接口中的sendSimpleMessage方法，该方法接收收件人邮箱地址to、
     * 邮件主题 subject 和邮件内容 text 作为参数。
     * 在sendSimpleMessage方法内部，创建了一个SimpleMailMessage对象
     * simpleMailMessage，并设置发件人、收件人、主题和邮件内容。
     * 最后调用 mailSender.send(simpleMailMessage) 方法发送邮件。
     * @param to
     * @param subject
     * @param text
     */
    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(Constant.EMAIL_FROM);
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(text);
        mailSender.send(simpleMailMessage);
    }
}
