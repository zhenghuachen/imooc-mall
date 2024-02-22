package com.imooc.mall.service;

import com.imooc.mall.model.vo.CartVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述： 邮件Service
 */
@Service
public interface EmailService {

    void sendSimpleMessage(String to, String subject,String text);

    // 将验证码和邮箱保存到Redis中
    Boolean saveEmailToRedis(String emailAddress, String verificationCode);
}
