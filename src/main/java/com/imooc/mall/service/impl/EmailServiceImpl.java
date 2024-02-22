package com.imooc.mall.service.impl;

import com.imooc.mall.common.Constant;
import com.imooc.mall.service.EmailService;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    /**
     * 定义一个公共的方法 saveEmailToRedis，接收两个参数，分别是邮箱地址 emailAddress 和验证码 verificationCode，返回一个布尔值。
     * 1.创建一个 Redisson 客户端对象 client，用于连接 Redis 数据库。
     * 2.通过 Redisson 客户端对象 client 获取指定键名为 emailAddress 的 RBucket 对象 bucket，用于操作 Redis 中的字符串数据。
     * 3.检查 Redis 中是否已经存在键名为 emailAddress 的数据，并将结果存储在布尔变量 exist 中。
     * 4.如果不存在键名为 emailAddress 的数据，将验证码 verificationCode 存储到 Redis 的 emailAddress 键中，并设置过期时间为 60 秒。
     * 5.如果键名为 emailAddress 的数据已经存在（即 exist 为 true），则直接返回布尔值 false，表示验证码未保存到 Redis 中。
     * @param emailAddress
     * @param verificationCode
     * @return
     */
    // 将验证码和邮箱保存到Redis中
    @Override
    public  Boolean saveEmailToRedis (String emailAddress, String verificationCode) {
        RedissonClient client = Redisson.create();
        RBucket<String> bucket = client.getBucket(emailAddress);
        boolean exist = bucket.isExists();;
        if (!exist) {
           bucket.set(verificationCode, 60, TimeUnit.SECONDS);
           return true;
        }
        return false;
    }

    @Override
    public Boolean checkEmailAndCode (String emailAddress, String verificationCode) {
        // 和Redis建立连接
        RedissonClient client = Redisson.create();
        RBucket<String> bucket = client.getBucket(emailAddress);
        boolean exist = bucket.isExists();;
        if (exist) {  // 说明验证码在Redis中有存储
            String code = bucket.get(); //验证码
            // redis里存储的验证码，和用户传过来的一致，则校验通过
            if (code.equals(verificationCode)) {  // verificationCode为用户传入测验证码
                return true;
            }
        }
        return false;
    }
}
