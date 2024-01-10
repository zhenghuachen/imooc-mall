package com.imooc.mall.config;

import com.imooc.mall.filter.AdminFilter;
import com.imooc.mall.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述： User过滤器的配置
 */
@Configuration
public class UserFilterConfig {

    // 1.定义Filter
    @Bean
    public UserFilter userFilter() {
        return new UserFilter();
    }
    // 2.将Filter放入过滤器的链路中
    @Bean(name = "userFilterConf")  //userFilterConf避免于类名相同，冲突
    public FilterRegistrationBean adminFilterConfig() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(userFilter());  // 设置过滤器调用的是userfilter()方法
        filterRegistrationBean.addUrlPatterns("/cart/*");  // 购物车
        filterRegistrationBean.addUrlPatterns("/order/*"); // 订单相关
        filterRegistrationBean.setName("userFilterConf");
        return filterRegistrationBean;
    }
}
