package com.imooc.mall.controller;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallException;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.EmailService;
import com.imooc.mall.service.UserService;
import com.imooc.mall.util.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.AddressException;
import javax.servlet.http.HttpSession;

/**
 * 用户控制器
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    EmailService emailService;

    @GetMapping("/test")
    @ResponseBody
    public User personalPage() {
        return userService.getUser();
    }

    /**
     * 注册方法
     * 注册这个方法的返回是ApiRestResponse
     */
    @PostMapping("/register")  // 定义URL
    @ResponseBody // 返回JSON
    public ApiRestResponse register(@RequestParam("userName") String userName, @RequestParam("password") String password) throws ImoocMallException {
        // userName和password不能为空校验
        if(StringUtils.isEmpty(userName)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        // 密码长度不能小于8位
        if(password.length()<8){
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        userService.register(userName, password);
        return ApiRestResponse.success();
    }

    /**
     * 登录接口
     * 传参除了userName和password增加HttpSession 用来记录登录状态
     */
    @PostMapping("/login") // 定义URL
    @ResponseBody  //Spring会自动将Controller方法的返回值转换为适当的响应格式（如JSON、XML等），并将其作为HTTP响应的实体内容返回。
    public ApiRestResponse login(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws ImoocMallException {
        // userName和password不能为空校验
        if(StringUtils.isEmpty(userName)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        // 密码长度不能小于8位
        if(password.length()<8){
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        User user = userService.login(userName,password);
        user.setPassword(null);  // 保存用户信息时，不保存密码；避免password被直接返回，导致不安全
        /**
         * session.setAttribute()方法用于将数据存储到会话中。会话是Web应用程序中存储用户数据的对象，通常用于保持用户状态和数据在多个请求之间。
         * session.setAttribute()方法接受两个参数：第一个参数是要存储的数据，第二个参数是一个键，用于标识数据。
         * 当使用session.setAttribute()方法时，Spring框架会自动将数据存储到会话中，并使用指定的键进行标识。
         */
        session.setAttribute(Constant.IMOOC_MALL_USER, user);
        return ApiRestResponse.success(user);
    }

    /**
     * 更新个性签名
     * 由于signature来源于入参，需要增加@RequestParam注解
     */
    @PostMapping("/user/update")  // 定义URL
    @ResponseBody
    public ApiRestResponse updateUserInfo(HttpSession session, @RequestParam String signature) throws ImoocMallException {
        // session.getAttribute()查找与键关联的数据，并将其作为方法返回值返回
        User currentUser = (User)session.getAttribute(Constant.IMOOC_MALL_USER);
        // 没有找到用户登录信息
        if (currentUser == null) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN);
        }
        User user = new User();  //新建用户对象
        user.setId(currentUser.getId());   // 设置id
        user.setPersonalizedSignature(signature); // 设置个性签名
        userService.updateInformation(user);  //需要UserServiceImpl中新增updateInformation方法
        return ApiRestResponse.success();
    }

    /**
     * 登出，清除session
     */
    @PostMapping("/user/logout")
    @ResponseBody
    public ApiRestResponse logout(HttpSession session){
        // session.removeAttribute()方法用于从会话中删除数据。会话是Web应用程序中存储用户数据的对象，通常用于保持用户状态和数据在多个请求之间。
        session.removeAttribute(Constant.IMOOC_MALL_USER);
        return ApiRestResponse.success();
    }

    /**
     * 管理员登录接口
     *
     */
    @PostMapping("/adminLogin")
    @ResponseBody
    public ApiRestResponse adminLogin(@RequestParam("userName") String userName, @RequestParam("password") String password, HttpSession session) throws ImoocMallException {
        // userName和password不能为空校验
        if(StringUtils.isEmpty(userName)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_USER_NAME);
        }
        if(StringUtils.isEmpty(password)){
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_PASSWORD);
        }
        // 密码长度不能小于8位
        if(password.length()<8){
            return ApiRestResponse.error(ImoocMallExceptionEnum.PASSWORD_TOO_SHORT);
        }
        User user = userService.login(userName,password);
        // 校验是否为管理员,UserServiceimpl增加checkAdminRole()方法
        if (userService.checkAdminRole(user)) {
            // 是管理员，执行操作
            // 保存用户信息，不保存密码
            user.setPassword(null);  // 保存用户信息时，不保存密码；避免password被直接返回，导致不安全
            session.setAttribute(Constant.IMOOC_MALL_USER, user);
            return ApiRestResponse.success(user);
        } else {
            return ApiRestResponse.error(ImoocMallExceptionEnum.NEED_ADMIN);
        }
    }

    /**
     * 发送邮件
     *
     */
    @PostMapping("/user/sendEmail")
    @ResponseBody
    public ApiRestResponse sendEmail(@RequestParam("emailAddress") String emailAddress) throws ImoocMallException, AddressException {
        // 检查地址是否有效， 检查是否已注册
        boolean validEmailAddress = EmailUtil.isValidEmailAddress(emailAddress);
        if (validEmailAddress) {
            boolean emailPassed = userService.checkEmailRegistered(emailAddress);
            if (!emailPassed) {
                return ApiRestResponse.error(ImoocMallExceptionEnum.EMAIL_ALREADY_BEEN_REGISTERED);
            } else {
                // 发送邮件
                emailService.sendSimpleMessage(emailAddress, Constant.EMAIL_SUBJECT, "欢迎注册，您的验证码是");
                return  ApiRestResponse.success();
            }
        } else {
            return  ApiRestResponse.error(ImoocMallExceptionEnum.WRONG_EMAIL);
        }
    }

}
