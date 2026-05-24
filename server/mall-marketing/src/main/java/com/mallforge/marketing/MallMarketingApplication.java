package com.mallforge.marketing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.mallforge.marketing.mapper")
@ComponentScan(basePackages = { "com.mallforge.marketing", "com.mallforge.framework", "com.mallforge.common" })
public class MallMarketingApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallMarketingApplication.class, args);
    }
}
