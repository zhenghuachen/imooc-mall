package com.imooc.mall.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

}
