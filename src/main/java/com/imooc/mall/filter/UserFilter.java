package com.imooc.mall.filter;

import com.imooc.mall.common.Constant;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 描述：用户过滤器
 */

public class UserFilter implements Filter {
    // 静态变量保存currentUser
    public static User currentUser; // 本次课程暂不考虑线程安全问题
    @Autowired
    UserService userService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /**
         * Spring MVC中获取HttpSession对象的方法
         * 首先，将servletRequest对象转换为HttpServletRequest对象，以便后续操作;
         * 然后，使用request对象获取当前请求的HttpSession对象。getSession()方法会根据当前请求的会话ID查找会话，
         * 如果找不到，则创建一个新的会话并返回。
         * 这样，我们就可以通过session对象来操作会话数据，例如保存用户登录信息、获取用户登录信息等。
         */
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpSession session = request.getSession();
        currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);   //保存用户ID
        if (currentUser == null){  // 检验是否登录,未登录则
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                    + "    \"status\": 10007,\n"
                    + "    \"msg\": \"NEED_LOGIN\",\n"
                    + "    \"data\": null\n"
                    + "}");
            out.flush();
            out.close();
            return;
        }
        // 通过校验，则原有逻辑继续执行
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
