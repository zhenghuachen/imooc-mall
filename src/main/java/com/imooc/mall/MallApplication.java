package com.imooc.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(basePackages = "com.imooc.mall.model.dao")
@EnableSwagger2
@EnableCaching // @EnableCaching会使Spring Boot自动配置缓存相关的依赖和组件，以支持缓存功能的启用和配置
public class MallApplication {

	public static void main(String[] args) {
		System.out.println(TimeZone.getDefault());
		SpringApplication.run(MallApplication.class, args);
	}

}
