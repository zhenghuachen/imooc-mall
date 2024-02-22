package com.imooc.mall.util;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Email工具
 */
public class EmailUtil {
    /**
     * 1.创建一个包含各种可能验证码字符的列表 verificationChars，包括数字 0 到 9 和大写字母 A 到 Z 以及小写字母 a 到 z。
     * 2.使用 Collections.shuffle 方法对 verificationChars 列表进行随机打乱，打乱后的列表顺序不固定。
     * 3.初始化一个空字符串变量 result，用于存储生成的验证码。开始一个 for 循环，循环次数为 6 次，目的是生成一个长度为 6 的验证码。
     * @return
     */
    public static String genVerificationCode() {
        List<String> verificationChars = Arrays.asList(
                new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
                        "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"});
        Collections.shuffle(verificationChars);
        String result = "";
        for (int i = 0; i < 6; i++) {
            result += verificationChars.get(i);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(EmailUtil.genVerificationCode());
    }

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
