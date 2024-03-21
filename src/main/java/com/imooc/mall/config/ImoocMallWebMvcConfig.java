package com.imooc.mall.config;

import com.imooc.mall.common.Constant;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 描述： 配置地址映射
 * WebMvcConfigurer接口定义了一系列方法，用于配置Spring MVC的属性:如视图解析器、拦截器、处理器映射器等。
 * 在Spring MVC项目中，我们可以通过实现WebMvcConfigurer接口的类来配置Spring MVC的各种属性。例如，
 * 我们可以实现ViewResolutionConfigurer接口来配置视图解析器，实现InterceptorRegistry接口来配置
 * 拦截器，实现MessageConverter接口来配置消息转换器等。
 */
@Configuration
public class ImoocMallWebMvcConfig implements WebMvcConfigurer {
    /**
     * addResourceHandlers(ResourceHandlerRegistry registry)方法是WebMvcConfigurer接口中
     * 定义的一个方法，用于向Spring MVC的资源处理器注册表中添加资源处理器。
     * 资源处理器用于处理静态资源，如CSS、JavaScript、图片等。在Spring Mvc中，可以通过配置资源处理器
     * 来指定静态资源的处理逻辑，例如指定静态资源的路径、指定静态资源的映射URL等。
     * 首先使用addResourceHandler方法添加了一个名为"swagger-ui.html"的资源处理器，并指定了处理路径
     * 为classpath:/META-INF/resources/下的文件。这意味着，当用户请求"swagger-ui.html"文件时，
     * Spring MVC会将其处理为该文件的内容。
     * 接下来，我们使用addResourceHandler方法添加了一个名为"/webjars/"的资源处理器，并指定了处理路径
     * 为classpath:/META-INF/resources/webjars/下的文件。这意味着，当用户请求"/webjars/"路径下
     * 的文件时，Spring MVC会将其处理为该文件的内容。
     * 这样，我们就可以将静态资源（如CSS、JavaScript、图片等）放在classpath:/META-INF/resources/和
     * classpath:/META-INF/resources/webjars/目录下，并通过"swagger-ui.html"和"/webjars/**"
     * 路径访问它们。
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /**
         * 使用Spring框架创建一个资源处理器。资源处理器用于处理请求中的URL，并将请求转发到相应的资源位置。
         * 首先使用registry.addResourceHandler()方法创建一个资源处理器，并将请求的URL路径设置为
         * /images/**，表示处理所有以/images/开头的请求。
         * 然后，使用addResourceLocations()方法将请求转发到指定的资源位置。资源位置设置为file:加上常量
         * Constant.FILE_UPLOAD_DIR，表示请求将被转发到指定的文件上传目录。
         * 最后，registry.addResourceHandler()将创建的资源处理器注册到Spring容器中，以便处理请求时使用。
         * 需要注意的是，这个例子中的资源处理器只处理以/images/开头的请求，并将请求转发到指定的文件上传目录。
         * 如果需要处理其他类型的请求或资源，需要使用相应的URL路径和资源位置进行配置。
         */
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + Constant.FILE_UPLOAD_DIR);
        registry.addResourceHandler("swagger-ui.html").addResourceLocations(
                "classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations(
                "classpath:/META-INF/resources/webjars/");
    }
    @Override
    // 公共方法用于配置CORS跨域访问规则，接受一个CorsRegistry对象作为参数，用于配置CORS规则
    public void addCorsMappings(CorsRegistry registry) {
        // registry对象调用addMapping方法，指定允许跨域请求的路径为所有路径（"/**"）
        registry.addMapping("/**")
                .allowedOrigins("*")  // 设置允许跨域请求的来源，此处为允许所有来源
                .allowedMethods("GET", "POST", "PUT","OPTIONS", "DELETE")  // 设置允许的HTTP请求方法
                .maxAge(3600)  // 设置预检请求的有效期，单位为秒
                .allowCredentials(true);  // 设置是否允许请求携带认证信息（如cookies、HTTP认证及客户端SSL证明）,此处允许请求携带认证信息
    }
}
