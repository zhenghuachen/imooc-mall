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
}
