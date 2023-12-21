package com.imooc.mall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * SpringFoxConfig类是一个实现Swagger2Configurer接口的类，它用于配置Swagger 2。
 * Swagger是一个用于生成API文档的工具，它可以自动生成API的HTML文档，并提供API的交互信息。
 * @Configuration用于标识一个类是一个配置类。配置类是一个用于配置Spring容器中各种Bean的类，
 * 例如视图解析器、拦截器、处理器映射器等
 */
@Configuration
public class SpringFoxConfig {

    // 访问http://localhost:8083/swagger-ui.html可以看到API文档
    // 配置Swagger 2的API文档
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())   //apiInfo()方法来设置API文档的元数据信息, 标题、描述、版本号等。
                .select()  // select()方法，用于指定要生成API文档的API
                .apis(RequestHandlerSelectors.any())  // RequestHandlerSelectors.any()来表示我们要生成所有Controller层的API
                .paths(PathSelectors.any())   // PathSelectors.any()来表示我们要生成所有路径的API
                .build();  // build()方法，用于构建Docket对象
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("慕慕生鲜")
                .description("")
                .termsOfServiceUrl("")
                .build();
    }
}