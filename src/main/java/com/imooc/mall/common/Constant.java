package com.imooc.mall.common;

import com.google.common.collect.Sets;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 常量值
 * @Component用于标识一个类作为一个Bean组件。当一个类被标记为@Component时，
 * Spring会自动扫描该类，并将其实例化为一个Bean组件。
 */
@Component
public class Constant {
    public static final String IMOOC_MALL_USER = "imooc_mall_user";

    // MD5 加盐
    public static final String SALT ="8svbsvjkweDF,.03[";
    public static final String EMAIL_SUBJECT = "您的验证码";
    public static final String EMAIL_FROM = "1003569727@qq.com";

    /** 此处有坑，静态变量注入是指在程序运行时，将外部数据注入到静态变量中
     *  在Java中，静态变量是类级别的变量，它们在类加载时初始化，并且只能被类对象访问。
     *  要实现静态变量注入，可以使用反射机制或类加载器实现。
     *  反射机制允许程序在运行时检查对象的类型、属性和方法，因此可以动态地设置静态变量的值。
     */
    // @Value("${file.upload.dir}")  不能通过Value直接注入
    public static String FILE_UPLOAD_DIR;

    @Value("${file.upload.dir}")
    public void setFileUploadDir(String fileUploadDir) {
        FILE_UPLOAD_DIR = fileUploadDir;
    }

    // 支持的排序字段
    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price desc", "price asc");
    }

    public  interface SaleStatus {
        int NOT_SALE = 0; // 商品下架状态
        int SALE = 1; // 商品上架状态
    }

    public  interface Cart {
        int UN_CHECKED = 0; // 购物车未选中状态
        int CHECKED = 1; // 购物车选中状态
    }

    public  enum OrderStatusEnum {

        CANCELED(0, "用户已取消"),
        NOT_PAID(10, "未付款"),
        PAID(20, "已付款"),
        DELIVERED(30, "已发货"),
        FINISHED(40, "交易完成");

        private String value;
        private int code;

        OrderStatusEnum(int code, String value) {
            this.value = value;
            this.code = code;
        }

        // 通过code获取描述
        public static OrderStatusEnum codeOf (int code) {
            for(OrderStatusEnum orderStatusEnum:values()) {
                if(orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_ENUM);
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }
    public static final String JWT_KEY = "imooc-mall";
    public static final String JWT_TOKEN = "jwt_token";
    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_ROLE = "user_role";
    public static final Long EXPIRE_TIME = 60 * 1000 * 60 * 24 * 1000L;   // 单位毫秒

}
