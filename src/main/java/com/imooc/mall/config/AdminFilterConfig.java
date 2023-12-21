package com.imooc.mall.config;

import com.imooc.mall.filter.AdminFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述： Admin过滤器的配置
 */
@Configuration
public class AdminFilterConfig {

    // 1.定义Filter
    @Bean
    public AdminFilter adminFilter() {
        return new AdminFilter();
    }
    // 2.将Filter放入过滤器的链路中
    @Bean(name = "adminFilterConf")  //adminFilterConf避免于类名相同，冲突
    public FilterRegistrationBean adminFilterConfig() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(adminFilter());  // 设置过滤器
        filterRegistrationBean.addUrlPatterns("/admin/category/*");
        filterRegistrationBean.addUrlPatterns("/admin/product/*");
        filterRegistrationBean.addUrlPatterns("/admin/order/*");
        filterRegistrationBean.setName("adminFilterConf");
        return filterRegistrationBean;
    }
}
