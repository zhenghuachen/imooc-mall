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
            InternetAddress internetAddress = new InternetAddress(email);
            internetAddress.validate();
        } catch (AddressException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }
}
