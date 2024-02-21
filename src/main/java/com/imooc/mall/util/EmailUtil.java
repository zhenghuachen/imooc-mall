package com.imooc.mall.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Email工具
 */
public class EmailUtil {

    public static boolean isValidEmailAddress(String email) throws AddressException {
        boolean result = true;

        try {
            /**
             * InternetAddress类是JavaMail API中的一部分，用于处理邮件地址相关的操作
             * 通过创建InternetAddress对象来尝试解析传入的email地址。
             * 如果解析成功，则调用internetAddress.validate()方法，该方法会验证邮箱
             * 地址的格式是否正确，如果邮箱地址格式正确，不会抛出异常。
             */
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (AddressException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
