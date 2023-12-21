package com.imooc.mall.filter;

import com.imooc.mall.common.ApiRestResponse;
import com.imooc.mall.common.Constant;
import com.imooc.mall.exception.ImoocMallExceptionEnum;
import com.imooc.mall.model.pojo.Category;
import com.imooc.mall.model.pojo.User;
import com.imooc.mall.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 描述：管理员校验过滤器
 */

public class AdminFilter implements Filter {
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
        User currentUser = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
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
        // 校验是否为管理员
        boolean adminRole = userService.checkAdminRole(currentUser);
        if (adminRole) {
            // 是管理员，执行放行操作
            /**
             * Spring MVC中的过滤器链处理请求的方法
             * 在Spring MVC中，每个请求都会经过一个过滤器链，过滤器链中的每个过滤器都有它的过滤逻辑。
             * 当请求到达过滤器链时，会按照过滤器的顺序依次进行过滤。如果过滤器返回true，则表示请求被
             * 通过，否则表示请求被拦截。
             * filterChain是一个过滤器链对象，它包含了多个过滤器。doFilter方法是过滤器链处理请求
             * 的方法，它接收servletRequest和servletResponse作为参数，并将请求传递给下一个过滤器
             * 进行处理。如果所有过滤器都返回true，则请求将被允许通过，否则将被拦截。
             */
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            /**
             * Spring MVC中向响应中写入内容的方法
             * 这段代码获取HttpServletResponse对象，并将其包装为HttpServletResponseWrapper
             * 对象，以便向响应中写入内容.
             * HttpServletResponseWrapper是一个包装类，它包装了一个HttpServletResponse对象，
             * 提供了向响应中写入内容的方法。
             * getWriter()方法是HttpServletResponseWrapper类中的方法，它返回一个PrintWriter
             * 对象，用于向响应中写入内容。
             * out.flush()方法将缓冲区的内容刷新到响应中;
             * out.close()方法关闭PrintWriter对象，完成响应的写入。
             */
            PrintWriter out = new HttpServletResponseWrapper((HttpServletResponse) servletResponse).getWriter();
            out.write("{\n"
                    + "    \"status\": 10009,\n"
                    + "    \"msg\": \"NEED_ADMIN\",\n"
                    + "    \"data\": null\n"
                    + "}");
            out.flush();
            out.close();
        }
    }
}
