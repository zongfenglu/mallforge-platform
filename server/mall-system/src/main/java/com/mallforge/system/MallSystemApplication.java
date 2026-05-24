package com.mallforge.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.mallforge.system")
@ComponentScan(basePackages = { "com.mallforge.system", "com.mallforge.framework", "com.mallforge.common" })
public class MallSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallSystemApplication.class, args);
    }
}
